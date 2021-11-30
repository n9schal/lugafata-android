package com.nischal.clothingstore.ui.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nischal.clothingstore.*
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

@Entity(
    tableName = "productVariants"
)
data class ProductVariant(
    @PrimaryKey
    val productVariantId: String = "",
    val productId: String = "",
    var orderLineId: String = "",
    val productVariantName: String = "",
    val productVariantSku: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    var productVariantPrice: Int = 0,
    val productVariantStockLevel: String = "",
    val productVariantDescription: String = "",
    val productVariantDiscountPercent: Int = 0,
    val productVariantFeaturedAsset: Image = Image(),
    val productVariantAssets: List<Image> = listOf(),
    var qtyInCart: Int = 0,
    val facetValueIds: List<String> = listOf(),
    val options: List<Option> = listOf()
) : Serializable {
    companion object {
        fun parseToProductVariants(data: ArrayList<ProductQuery.Variant>): ArrayList<ProductVariant> {
            val productVariants = arrayListOf<ProductVariant>()
            data.forEach { variant ->
                val imageUrl: String = if (variant.featuredAsset != null) {
                    variant.featuredAsset.source
                } else {
                    ""
                }
                // * add image assets
                val images = arrayListOf<Image>()
                variant.assets.forEach { asset -> images.add(Image(src = asset.source)) }
                // * add options
                val options = arrayListOf<Option>()
                variant.options.forEach { option ->
                    options.add(
                        Option(
                            optionId = option.id,
                            optionCode = option.code,
                            optionName = option.name
                        )
                    )
                }

                val tempVariant = ProductVariant(
                    productVariantId = variant.id,
                    productId = variant.productId,
                    productVariantName = variant.name,
                    productVariantSku = variant.sku,
                    createdAt = variant.createdAt.toString(),
                    updatedAt = variant.updatedAt.toString(),
                    productVariantPrice = variant.priceWithTax / 100,
                    productVariantStockLevel = variant.stockLevel,
                    productVariantFeaturedAsset = Image(src = imageUrl),
                    productVariantAssets = images,
                    options = options
                )
                productVariants.add(tempVariant)
            }
            return productVariants
        }
    }
}

data class Product(
    var productId: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val productName: String = "",
    val productPrice: Int = 0,
    val productSlug: String = "",
    val productDescription: String = "",
    var productFeaturedAsset: Image = Image(),
    val productAssets: ArrayList<Image> = arrayListOf(),
    val optionGroups: ArrayList<OptionGroup> = arrayListOf(),
    val productVariants: ArrayList<ProductVariant> = arrayListOf()
) : Serializable {

    companion object {

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
                    productPrice = price / 100,
                    productSlug = searchResult.slug,
                    productDescription = searchResult.description,
                    productFeaturedAsset = Image(src = imageUrl)
                )
                products.add(product)
            }
            return products
        }

        fun parseToProduct(data: ProductQuery.Product): Product {
            // * parse option groups and options
            val optionGroups =
                OptionGroup.parseToOptionGroups(data.optionGroups as ArrayList<ProductQuery.OptionGroup>)

            // * parse productVariants
            val productVariants =
                ProductVariant.parseToProductVariants(data.variants as ArrayList<ProductQuery.Variant>)

            val imageUrl = if (data.featuredAsset != null) {
                data.featuredAsset.source
            } else {
                ""
            }

            val images = arrayListOf<Image>()
            data.assets.forEach { asset -> images.add(Image(src = asset.source)) }

            return Product(
                productId = data.id,
                createdAt = data.createdAt.toString(),
                updatedAt = data.updatedAt.toString(),
                productName = data.name,
                productSlug = data.slug,
                productDescription = data.description,
                optionGroups = optionGroups,
                productVariants = productVariants,
                productFeaturedAsset = Image(src = imageUrl),
                productAssets = images
            )
        }
    }
}

// * option category like size and color
data class OptionGroup(
    val optionGroupId: String = "",
    val optionGroupCode: String = "",
    val optionGroupName: String = "",
    val options: ArrayList<Option> = arrayListOf()
) {
    companion object {
        fun parseToOptionGroups(data: ArrayList<ProductQuery.OptionGroup>): ArrayList<OptionGroup> {
            val optionGroups = arrayListOf<OptionGroup>()
            data.forEach { optionGroup ->
                val options = arrayListOf<Option>()
                optionGroup.options.forEach { option ->
                    val tempOption = Option(
                        optionId = option.id,
                        optionCode = option.code,
                        optionName = option.name
                    )
                    options.add(tempOption)
                }
                val tempOptionGroup = OptionGroup(
                    optionGroupId = optionGroup.id,
                    optionGroupCode = optionGroup.code,
                    optionGroupName = optionGroup.name,
                    options = options
                )
                optionGroups.add(tempOptionGroup)
            }
            return optionGroups
        }
    }
}

// * option values like XL and Brown
data class Option(
    val optionId: String = "",
    val optionCode: String = "",
    val optionName: String = ""
)

data class Image(
    var src: String = "",
    val title: String = ""
) : Serializable

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
                    val categoryImageUrl = if (collection.featuredAsset != null) {
                        collection.featuredAsset.source
                    } else {
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

data class OrderDetails(
    var id: String = "",
    var orderNumber: String = "",
    var productList: List<Product> = arrayListOf(),
    var subTotal: Int = 0,
    var deliveryCharge: Int = 0,
    var total: Int = 0,
    var deliveryLocation: Location? = null,
    var deliveryNote: String = "",
    var paymentOption: PaymentOption? = null,
    val createdAt: String = "",
    val completedDate: String? = null,
    var vendureOrderState: String = "",
    val status: Int = 0,
    var createdBy: String = "",
    var phoneNumber: String = "",
    var assignedTo: String = ""
) : Serializable {
    companion object {

    }
}

data class PaymentOption(
    val paymentName: String = "",
    var isSelected: Boolean = false
) {
    companion object {
        fun getPaymentOptions() = arrayListOf(
            PaymentOption(Constants.PaymentOptions.CASH_ON_DELIVERY, true)
        )
    }
}

data class SubCategory(
    val subCategoryId: String = "",
    val parentCategoryId: String = "",
    val subCategorySlug: String = "",
    val subCategoryName: String = "",
    val image: Image = Image()
)

data class SearchResponse(
    val products: ArrayList<Product> = arrayListOf(),
    val facetValues: List<SearchProductsQuery.FacetValue>
)

data class Location(
    var savedLocationName: String = "",
    var streetLine1: String = "",
    var city: String = "",
    var countryCode: String = "NP",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var isSelected: Boolean = false
)