package com.nischal.clothingstore.ui.models

import androidx.room.PrimaryKey
import com.nischal.clothingstore.ActiveCustomerQuery
import com.nischal.clothingstore.CategoryCollectionsQuery
import com.nischal.clothingstore.HomePageCollectionsQuery
import com.nischal.clothingstore.SearchProductsQuery
import com.nischal.clothingstore.utils.Constants
import com.nischal.clothingstore.utils.Constants.StockLevelConstants.OUT_OF_STOCK
import java.io.Serializable

data class RegisterRequest(
    val title: String? = null,
    val firstName: String,
    val lastName: String,
    val mobileNumber: String,
    val email: String,
    val password: String,
    val rePassword: String
)

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)

data class AlertMessage(
    val title: String = Constants.ValidationTitle.DEFAULT_TITLE,
    val message: String
)

data class UserDetails(
    var id: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = ""
) {
    companion object {
        fun parseToUserDetails(data: ActiveCustomerQuery.ActiveCustomer): UserDetails {
            return UserDetails(
                id = data.id,
                displayName = data.firstName + " " + data.lastName,
                firstName = data.firstName,
                lastName = data.lastName,
                phoneNumber = data.phoneNumber ?: "",
                email = data.emailAddress
            )
        }
    }
}

data class ProductVariant(
    @PrimaryKey
    var productVariantId: String = "",
    var orderLineId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val productVariantName: String = "",
    var productVariantPrice: Int = 0,
    val productVariantStockLevel: String = "",
    val productVariantDescription: String = "",
    val productVariantDiscountPercent: Int = 0,
    val productVariantFeaturedAsset: Image = Image(),
    val productVariantAssets: List<Image> = arrayListOf(),
    var qtyInCart: Int = 0,
    val facetValueIds: List<String> = listOf()
)

data class Product(
    var productId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val productSlug: String = "",
    val productDescription: String = "",
    val productFeaturedAsset: Image = Image(),
    val productAssets: List<Image> = arrayListOf(),
    // * option category like size and color
    val optionGroups: List<String> = arrayListOf(),
    // * option values like XL and Brown
    val options: List<String> = arrayListOf(),
    val productVariants: List<ProductVariant> = arrayListOf()
) {

    companion object {
        fun removeTrailingZeroInPrice(price: Int) = price / 100

        fun isInStock(productVariants: List<ProductVariant>): Boolean {
            var isInStock = false
            if (!productVariants.isNullOrEmpty()) {
                // * check if all the product variants is out of stock or not
                for (productVariant in productVariants) {
                    if (productVariant.productVariantStockLevel != OUT_OF_STOCK) {
                        isInStock = true
                        break
                    }
                }
            }
            return isInStock
        }

        fun parseToProductList(data: SearchProductsQuery.Data): ArrayList<Product> {
            val products = arrayListOf<Product>()
            data.search.items.forEach { searchResult ->
                val imageUrl: String = if (searchResult.productAsset != null) {
                    searchResult.productAsset.preview
                } else {
                    ""
                }
                val price: Int = if (searchResult.priceWithTax.asSinglePrice != null) {
                    searchResult.priceWithTax.asSinglePrice.value
                } else {
                    searchResult.priceWithTax.asPriceRange?.max ?: 0
                }

                val product = Product(
                    productId = searchResult.productId,
                    productName = searchResult.productName,
                    productPrice = removeTrailingZeroInPrice(price),
                    productSlug = searchResult.slug,
                    productDescription = searchResult.description,
                    productFeaturedAsset = Image(src = imageUrl)
                )
                products.add(product)
            }
            return products
        }
    }
}

data class Image(
    var src: String = "",
    val title: String = ""
)

data class HomeCategory(
    val id: String = "",
    val title: String = "",
    val adUrl: String = "",
    val productList: ArrayList<Product> = arrayListOf()
) {
    companion object {
        fun parseToHomeCategories(data: HomePageCollectionsQuery.Data): ArrayList<HomeCategory> {
            val homeCategories = arrayListOf<HomeCategory>()
            data.collections.items.forEach { collection ->
                if (collection.parent?.slug == Constants.SlugConstants.HOME_PAGE) {
                    val homeCategory = HomeCategory(
                        id = collection.id,
                        title = collection.name
                    )
                    homeCategories.add(homeCategory)
                }
            }
            return homeCategories
        }
    }
}

data class Category(
    val categoryId: String = "",
    val categoryName: String = "",
    val collectionSlug: String = "",
    val parentCollectionSlug: String = "",
    val image: Image = Image(),
    val subCategories: ArrayList<SubCategory> = arrayListOf()
) : Serializable {
    companion object {
        fun parseToCategories(
            data: CategoryCollectionsQuery.Data
        ): ArrayList<Category> {
            val categories = arrayListOf<Category>()
            // * outer loop for category
            data.collections.items.forEach { collection ->
                if (collection.parent?.slug!!.contains(Constants.SlugConstants.CATEGORIES, true)
                    && !collection.children.isNullOrEmpty()
                ) {
                    val subCategories = arrayListOf<SubCategory>()
                    // * inner loop for subcategory
                    collection.children.forEach { subCategory ->
                        val subCategoryImageUrl = if (subCategory.featuredAsset != null) {
                            subCategory.featuredAsset.source
                        } else {
                            ""
                        }
                        val subCat = SubCategory(
                            subCategoryId = subCategory.id,
                            subCategoryName = subCategory.name,
                            subCategorySlug = subCategory.slug,
                            image = Image(src = subCategoryImageUrl),
                            parentCategoryId = collection.id
                        )
                        subCategories.add(subCat)
                    }
                    val categoryImageUrl = if(collection.featuredAsset != null){
                        collection.featuredAsset.source
                    }else {
                        ""
                    }
                    val category = Category(
                        categoryId = collection.id,
                        categoryName = collection.name,
                        collectionSlug = collection.slug,
                        parentCollectionSlug = collection.parent.slug,
                        image = Image(src = categoryImageUrl),
                        subCategories = subCategories
                    )
                    categories.add(category)
                }
            }
            return categories
        }
    }
}


data class SubCategory(
    val subCategoryId: String = "",
    val parentCategoryId: String = "",
    val subCategorySlug: String = "",
    val subCategoryName: String = "",
    val image: Image = Image()
)