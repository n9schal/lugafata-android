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

        fun parseToUserDetails(data: UpdateCustomerMutation.UpdateCustomer): UserDetails {
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

        /**
         * removes 2 digits from LSB as in price
         * @param price int value
         * @return int after removing trailing zeros
         * */
        fun removeTrailingZeroInPrice(price: Int) = price / 100

        /**
         * parse active order query results to list of [ProductVariant]
         * @param data active order fetch result [ProductQuery.Variant]
         * @return ArrayList of [ProductVariant]
         * */
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

        /**
         * parse active order query results to list of [ProductVariant]
         * @param data active order fetch result [ActiveOrderQuery.ActiveOrder]
         * @return ArrayList of [ProductVariant]
         * */
        fun parseToProductVariants(data: ActiveOrderQuery.ActiveOrder): ArrayList<ProductVariant> {
            val productVariants = arrayListOf<ProductVariant>()
            data.lines.forEach { orderline ->
                val imageUrl = if (orderline.productVariant.featuredAsset != null) {
                    orderline.productVariant.featuredAsset.source
                } else {
                    ""
                }
                // * get facet value ids
                val facetValueIds: ArrayList<String> = arrayListOf()
                orderline.productVariant.product.facetValues.forEach { facetValue ->
                    facetValueIds.add(facetValue.id)
                }

                // * add options
                val options = arrayListOf<Option>()
                orderline.productVariant.options.forEach { option ->
                    options.add(
                        Option(
                            optionId = option.id,
                            optionCode = option.code,
                            optionName = option.name
                        )
                    )
                }
                // * map to product model
                val productVariant = ProductVariant(
                    productVariantId = orderline.productVariant.id,
                    createdAt = orderline.productVariant.createdAt.toString(),
                    updatedAt = orderline.productVariant.updatedAt.toString(),
                    productVariantStockLevel = orderline.productVariant.stockLevel,
                    qtyInCart = orderline.quantity,
                    productVariantDescription = orderline.productVariant.product.description,
                    productVariantName = orderline.productVariant.name,
                    productVariantFeaturedAsset = Image(src = imageUrl),
                    productVariantPrice = removeTrailingZeroInPrice(
                        orderline.productVariant.priceWithTax
                    ),
                    facetValueIds = facetValueIds,
                    options = options
                )
                productVariants.add(productVariant)
            }
            return productVariants
        }

        /**
         * parse active order fetch results to list of [ProductVariant]
         * @param orderLines active order fetch result [ActiveCustomerQuery.Line]
         * @return ArrayList of [ProductVariant]
         * */
        fun parseToProductVariants(orderLines: List<ActiveCustomerQuery.Line>): ArrayList<ProductVariant> {
            val productVariants = arrayListOf<ProductVariant>()
            orderLines.forEach { orderline ->
                val imageUrl = if (orderline.productVariant.featuredAsset != null) {
                    orderline.productVariant.featuredAsset.source
                } else {
                    ""
                }
                // * get facet value ids
                val facetValueIds: ArrayList<String> = arrayListOf()
                orderline.productVariant.product.facetValues.forEach { facetValue ->
                    facetValueIds.add(facetValue.id)
                }
                // * map to product model
                val productVariant = ProductVariant(
                    productVariantId = orderline.productVariant.id,
                    createdAt = orderline.productVariant.createdAt.toString(),
                    updatedAt = orderline.productVariant.updatedAt.toString(),
                    productVariantStockLevel = orderline.productVariant.stockLevel,
                    qtyInCart = orderline.quantity,
                    productVariantDescription = orderline.productVariant.product.description,
                    productVariantName = orderline.productVariant.name,
                    productVariantFeaturedAsset = Image(src = imageUrl),
                    productVariantPrice = removeTrailingZeroInPrice(
                        orderline.productVariant.priceWithTax
                    ),
                    facetValueIds = facetValueIds
                )
                productVariants.add(productVariant)
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
    var productVariantList: List<ProductVariant> = arrayListOf(),
    var subTotal: Int = 0,
    var deliveryCharge: Int = 0,
    var total: Int = 0,
    var deliveryLocation: Location? = null,
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
        fun parseToOrderDetail(activeOrder: ActiveOrderQuery.ActiveOrder): OrderDetails {
            return OrderDetails(
                id = activeOrder.id,
                orderNumber = activeOrder.code,
                vendureOrderState = activeOrder.state,
                productVariantList = ProductVariant.parseToProductVariants(activeOrder),
                subTotal = activeOrder.subTotalWithTax / 100,
                deliveryCharge = activeOrder.shippingWithTax / 100,
                total = activeOrder.totalWithTax / 100
            )
        }

        fun parseToOrderDetails(data: ActiveCustomerQuery.Data): ArrayList<OrderDetails> {
            val orders = arrayListOf<OrderDetails>()
            data.activeCustomer?.orders?.items?.forEach { order ->
                val orderDetails = OrderDetails(
                    id = order.id,
                    orderNumber = order.code,
                    productVariantList = ProductVariant.parseToProductVariants(order.lines),
                    subTotal = order.subTotalWithTax / 100,
                    deliveryCharge = order.shippingWithTax / 100,
                    total = order.totalWithTax / 100,
                    deliveryLocation = Location(
                        streetLine1 = order.shippingAddress?.streetLine1 ?: "",
                        city = order.shippingAddress?.city ?: ""
                    ),
                    createdAt = order.createdAt.toString(),
                    vendureOrderState = order.state,
                    phoneNumber = data.activeCustomer.phoneNumber ?: ""
                )

                orders.add(orderDetails)
            }
            return orders
        }
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
    var extraNote: String = "",
    var streetLine1: String = "",
    var city: String = "",
    var countryCode: String = "NP",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var isSelected: Boolean = false
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val repeatPassword: String
)