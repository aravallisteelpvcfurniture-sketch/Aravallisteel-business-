package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Estimate::class,
        ConfirmedOrder::class,
        Visitor::class,
        Carpenter::class,
        AttendanceRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun estimateDao(): EstimateDao
    abstract fun confirmedOrderDao(): ConfirmedOrderDao
    abstract fun visitorDao(): VisitorDao
    abstract fun carpenterDao(): CarpenterDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aravalli_furniture_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database)
                    }
                }
            }
        }

        suspend fun seedInitialData(database: AppDatabase) {
            // Seed sample Confirmed Orders totaling ~₹662,092 matching the screenshot!
            val initialOrders = listOf(
                ConfirmedOrder(
                    orderNumber = "ORD-ARV-2026-101",
                    customerName = "Prakash Mewara",
                    customerPhone = "+91 98290 12345",
                    deliveryAddress = "Plot 42, Shreenath Nagar, Rajsamand",
                    productSummary = "PVC Modular L-Shape Kitchen (12x9 ft) with Acrylic Finish",
                    totalAmount = 145000.0,
                    advancePaid = 80000.0,
                    balanceDue = 65000.0,
                    orderDate = "2026-10-02",
                    deliveryDate = "2026-10-18",
                    stage = "Assembling",
                    paymentMode = "UPI / GPay"
                ),
                ConfirmedOrder(
                    orderNumber = "ORD-ARV-2026-102",
                    customerName = "Vikram Rathore",
                    customerPhone = "+91 94140 88219",
                    deliveryAddress = "Sector 14, Hiran Magri, Udaipur",
                    productSummary = "Heavy Gauge 3-Door Steel Almirah + Digital Locker (Matte Grey)",
                    totalAmount = 88500.0,
                    advancePaid = 50000.0,
                    balanceDue = 38500.0,
                    orderDate = "2026-10-05",
                    deliveryDate = "2026-10-14",
                    stage = "Ready for Dispatch",
                    paymentMode = "Cash"
                ),
                ConfirmedOrder(
                    orderNumber = "ORD-ARV-2026-103",
                    customerName = "Kailash Choudhary",
                    customerPhone = "+91 97845 33412",
                    deliveryAddress = "Near Bus Stand, Nathdwara",
                    productSummary = "PVC Waterproof Bathroom & Balcony Doors (6 Units) + Frames",
                    totalAmount = 64200.0,
                    advancePaid = 40000.0,
                    balanceDue = 24200.0,
                    orderDate = "2026-10-07",
                    deliveryDate = "2026-10-20",
                    stage = "Material Cutting",
                    paymentMode = "UPI / GPay"
                ),
                ConfirmedOrder(
                    orderNumber = "ORD-ARV-2026-104",
                    customerName = "Anil Somani",
                    customerPhone = "+91 98282 55901",
                    deliveryAddress = "Subhash Nagar, Bhilwara",
                    productSummary = "Complete PVC Bedroom Wardrobe (9x7 ft) with Loft Cabinets",
                    totalAmount = 175000.0,
                    advancePaid = 100000.0,
                    balanceDue = 75000.0,
                    orderDate = "2026-10-09",
                    deliveryDate = "2026-10-24",
                    stage = "Material Cutting",
                    paymentMode = "Cheque"
                ),
                ConfirmedOrder(
                    orderNumber = "ORD-ARV-2026-105",
                    customerName = "Mahaveer Jain",
                    customerPhone = "+91 94600 77123",
                    deliveryAddress = "Goverdhan Vilas, Udaipur",
                    productSummary = "Commercial Steel Storage Racks & File Cabinets (10 Units)",
                    totalAmount = 189392.0,
                    advancePaid = 120000.0,
                    balanceDue = 69392.0,
                    orderDate = "2026-10-11",
                    deliveryDate = "2026-10-28",
                    stage = "Order Confirmed",
                    paymentMode = "UPI / GPay"
                )
            )
            database.confirmedOrderDao().insertAll(initialOrders)

            // Seed sample Estimates
            val initialEstimates = listOf(
                Estimate(
                    customerName = "Prakash Mewara",
                    customerPhone = "+91 98290 12345",
                    customerAddress = "Rajsamand",
                    productType = "PVC Modular Kitchen",
                    dimensions = "12 x 9 ft",
                    areaSqFt = 108.0,
                    ratePerSqFt = 1350.0,
                    hardwareCost = 15000.0,
                    discountAmount = 15800.0,
                    totalAmount = 145000.0,
                    createdDate = "2026-10-01",
                    status = "Converted to Order",
                    notes = "Acrylic high gloss sheets requested"
                ),
                Estimate(
                    customerName = "Dr. Sanjay Bhatt",
                    customerPhone = "+91 94141 99011",
                    customerAddress = "Chetak Circle, Udaipur",
                    productType = "PVC False Ceiling & Wall Panels",
                    dimensions = "16 x 14 ft",
                    areaSqFt = 224.0,
                    ratePerSqFt = 110.0,
                    hardwareCost = 8000.0,
                    discountAmount = 2640.0,
                    totalAmount = 30000.0,
                    createdDate = "2026-10-04",
                    status = "Sent",
                    notes = "Waiting for site measurement approval"
                ),
                Estimate(
                    customerName = "Vikram Rathore",
                    customerPhone = "+91 94140 88219",
                    customerAddress = "Udaipur",
                    productType = "Steel Almirah (Wardrobe)",
                    dimensions = "6.5 x 4 ft",
                    areaSqFt = 26.0,
                    ratePerSqFt = 3400.0,
                    hardwareCost = 0.0,
                    discountAmount = 0.0,
                    totalAmount = 88500.0,
                    createdDate = "2026-10-04",
                    status = "Converted to Order",
                    notes = "18 gauge heavy steel with locker"
                ),
                Estimate(
                    customerName = "Smt. Leela Dave",
                    customerPhone = "+91 97833 44556",
                    customerAddress = "Kankroli, Rajsamand",
                    productType = "PVC Modular Kitchen",
                    dimensions = "10 x 8 ft",
                    areaSqFt = 80.0,
                    ratePerSqFt = 1400.0,
                    hardwareCost = 18000.0,
                    discountAmount = 10000.0,
                    totalAmount = 120000.0,
                    createdDate = "2026-10-06",
                    status = "Sent",
                    notes = "Client requested soft-close telescopic drawers"
                ),
                Estimate(
                    customerName = "Anil Somani",
                    customerPhone = "+91 98282 55901",
                    customerAddress = "Bhilwara",
                    productType = "PVC Bedroom Wardrobe",
                    dimensions = "9 x 7 ft",
                    areaSqFt = 63.0,
                    ratePerSqFt = 2500.0,
                    hardwareCost = 25000.0,
                    discountAmount = 7500.0,
                    totalAmount = 175000.0,
                    createdDate = "2026-10-08",
                    status = "Converted to Order",
                    notes = "Woodgrain textured PVC finish"
                ),
                Estimate(
                    customerName = "Gopal Krishna Gehlot",
                    customerPhone = "+91 94133 22114",
                    customerAddress = "Bedla, Udaipur",
                    productType = "PVC Solid Doors (10 Qty)",
                    dimensions = "7 x 3 ft",
                    areaSqFt = 210.0,
                    ratePerSqFt = 380.0,
                    hardwareCost = 12000.0,
                    discountAmount = 6000.0,
                    totalAmount = 85800.0,
                    createdDate = "2026-10-10",
                    status = "Sent",
                    notes = "For hotel resort project"
                )
            )
            database.estimateDao().insertAll(initialEstimates)

            // Seed sample Visitors
            val initialVisitors = listOf(
                Visitor(
                    name = "Manoj Purohit",
                    phone = "+91 98291 44521",
                    address = "Fatehpura, Udaipur",
                    visitDate = "2026-10-12",
                    interestedProduct = "PVC Modular Kitchen & Pantry",
                    budget = "₹1.5 Lakh - ₹2 Lakh",
                    priority = "Hot",
                    followUpDate = "2026-10-14",
                    notes = "Visited showroom with spouse. Liked grey marble textured PVC finish.",
                    status = "Active"
                ),
                Visitor(
                    name = "Rakesh Sharma",
                    phone = "+91 94142 88710",
                    address = "Kankroli",
                    visitDate = "2026-10-12",
                    interestedProduct = "Steel Almirah with Mirror & Locker",
                    budget = "₹35,000 - ₹50,000",
                    priority = "Hot",
                    followUpDate = "2026-10-13",
                    notes = "Looking for heavy 18-gauge steel wardrobe for wedding gift.",
                    status = "Active"
                ),
                Visitor(
                    name = "Bhavna Patel",
                    phone = "+91 97840 11984",
                    address = "Sector 4, Hiran Magri",
                    visitDate = "2026-10-11",
                    interestedProduct = "PVC Bathroom & Terrace Doors",
                    budget = "₹25,000",
                    priority = "Warm",
                    followUpDate = "2026-10-15",
                    notes = "Water leakage in old wooden door, wants 100% waterproof PVC.",
                    status = "Active"
                ),
                Visitor(
                    name = "Jitendra Vaishnav",
                    phone = "+91 94611 33452",
                    address = "Nathdwara",
                    visitDate = "2026-10-10",
                    interestedProduct = "PVC Fluted Wall Paneling for Drawing Room",
                    budget = "₹40,000",
                    priority = "Warm",
                    followUpDate = "2026-10-16",
                    notes = "Requested catalog PDF on WhatsApp.",
                    status = "Active"
                )
            )
            database.visitorDao().insertAll(initialVisitors)

            // Seed sample Carpenters
            val initialCarpenters = listOf(
                Carpenter(
                    name = "Ramesh Suthar (Master)",
                    phone = "+91 98299 11223",
                    location = "Kankroli & Rajsamand",
                    totalReferrals = 14,
                    pointsEarned = 1420,
                    totalCommissionEarned = 35500.0,
                    pendingPayout = 4500.0,
                    joinDate = "2025-06-15"
                ),
                Carpenter(
                    name = "Dinesh Mistri",
                    phone = "+91 94145 66778",
                    location = "Udaipur North",
                    totalReferrals = 9,
                    pointsEarned = 950,
                    totalCommissionEarned = 23750.0,
                    pendingPayout = 2500.0,
                    joinDate = "2025-08-20"
                ),
                Carpenter(
                    name = "Shyamlal Panchal",
                    phone = "+91 97821 44556",
                    location = "Nathdwara",
                    totalReferrals = 7,
                    pointsEarned = 720,
                    totalCommissionEarned = 18000.0,
                    pendingPayout = 1200.0,
                    joinDate = "2025-11-04"
                ),
                Carpenter(
                    name = "Kishore Kumar Jangid",
                    phone = "+91 94602 88990",
                    location = "Bhilwara Bypass",
                    totalReferrals = 5,
                    pointsEarned = 510,
                    totalCommissionEarned = 12750.0,
                    pendingPayout = 0.0,
                    joinDate = "2026-02-18"
                )
            )
            database.carpenterDao().insertAll(initialCarpenters)

            // Initial attendance
            database.attendanceDao().insertAttendance(
                AttendanceRecord(
                    date = "2026-10-12",
                    checkInTime = "09:00 AM",
                    checkOutTime = "",
                    isCheckedIn = true
                )
            )
        }
    }
}
