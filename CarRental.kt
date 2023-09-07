import java.math.BigDecimal

class Car(val title: String, val priceCode: PriceCode) {
    enum class PriceCode {
        ECONOMY, SUPERCAR, MUSCLE
    }
}

class Rental(val car: Car, val daysRented: Int) {
    private val pricings = mapOf(
        Car.PriceCode.ECONOMY to mapOf(
            "base" to BigDecimal("80"),
            "daily" to (((this.daysRented) - 2) * 30).toBigDecimal()
        ),
        Car.PriceCode.SUPERCAR to mapOf(
            "base" to BigDecimal.ZERO,
            "daily" to ((this.daysRented) * 200).toBigDecimal()
        ),
        Car.PriceCode.MUSCLE to mapOf(
            "base" to BigDecimal("200"),
            "daily" to (((this.daysRented) - 3) * 50).toBigDecimal()
        ),
    )
    
    fun determineAmount(): BigDecimal {
        var thisAmount = BigDecimal.ZERO.setScale(2)
        val pricing = pricings.getValue(this.car.priceCode)
        pricing.values.forEach { value ->
            thisAmount += value
        }
        return thisAmount
    }
}

class Customer(private val name: String) {
    private var _rentals = ArrayList<Rental>()

    fun addRental(arg: Rental) {
        _rentals.add(arg)
    }

    fun billingStatement(): String {
        var totalAmount = BigDecimal.ZERO.setScale(2)
        var frequentRenterPoints = 0
        val builder = StringBuilder()
        builder.append("Rental Record for ")
        builder.appendLine(name)
        for (rental in _rentals) {
            val thisAmount = rental.determineAmount()
            frequentRenterPoints += addFrequentRenterPoints(rental)
            //show figures for this rental
            builder.append("\t")
            builder.append(rental.car.title)
            builder.append("\t")
            builder.appendLine(thisAmount.toString())
            totalAmount += thisAmount
        }
        //add footer lines
        builder.appendLine("Final rental payment owed $totalAmount")
        builder.append("You received an additional $frequentRenterPoints frequent customer points")
        return builder.toString()
    }

    private fun addFrequentRenterPoints(rental: Rental): Int {
        var frequentRenterPoints = 1
        // add bonus for a two day new release rental
        if ((rental.car.priceCode == Car.PriceCode.SUPERCAR) && rental.daysRented > 1) {
            frequentRenterPoints += 1
        }
        return frequentRenterPoints
    }
}

fun main(){
    val rental1 = Rental(Car("Mustang", Car.PriceCode.MUSCLE), 5)
    val rental2 = Rental(Car("Lambo", Car.PriceCode.SUPERCAR), 20)
    val customer = Customer("Liviu")
    customer.addRental(rental1)
    customer.addRental(rental2)
    println(customer.billingStatement())
}