USE [master]
GO
/****** Object:  Database [RentalCar]    Script Date: 12/25/2024 12:14:42 PM ******/
CREATE DATABASE [RentalCar]
GO
USE [RentalCar]
GO
/****** Object:  Table [dbo].[AdditionalFunction]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[AdditionalFunction](
	[AdditionalFunctionId] [int] IDENTITY(1,1) NOT NULL,
	[functionName] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[AdditionalFunctionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Booking]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Booking](
	[bookingId] [int] IDENTITY(1,1) NOT NULL,
	[startDate] [datetime] NULL,
	[endDate] [datetime] NULL,
	[driverInfo] [nvarchar](255) NULL,
	[actualEndDate] [datetime] NULL,
	[totalPrice] [decimal](18, 2) NULL,
	[userId] [int] NULL,
	[bookingStatusId] [int] NULL,
	[paymentMethodId] [int] NULL,
	[lastModified] [datetime] NULL,
	[driverId] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[bookingId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BookingCar]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookingCar](
	[carId] [int] NOT NULL,
	[bookingId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[carId] ASC,
	[bookingId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[BookingStatus]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BookingStatus](
	[BookingStatusId] [int] NOT NULL,
	[name] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[BookingStatusId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Brand]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Brand](
	[BrandId] [int] IDENTITY(1,1) NOT NULL,
	[brandName] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[BrandId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Car]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Car](
	[carId] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](100) NULL,
	[licensePlate] [nvarchar](20) NULL,
	[model] [nvarchar](50) NULL,
	[color] [nvarchar](20) NULL,
	[seatNo] [int] NULL,
	[productionYear] [int] NULL,
	[transmission] [nvarchar](20) NULL,
	[fuel] [nvarchar](20) NULL,
	[mileage] [decimal](18, 2) NULL,
	[fuelConsumption] [decimal](18, 2) NULL,
	[basePrice] [decimal](18, 2) NULL,
	[deposit] [decimal](18, 2) NULL,
	[description] [nvarchar](max) NULL,
	[termOfUse] [nvarchar](max) NULL,
	[carPrice] [decimal](18, 2) NULL,
	[front] [nvarchar](200) NULL,
	[back] [nvarchar](200) NULL,
	[left] [nvarchar](200) NULL,
	[right] [nvarchar](200) NULL,
	[registration] [nvarchar](200) NULL,
	[certificate] [nvarchar](200) NULL,
	[insurance] [nvarchar](200) NULL,
	[lastModified] [datetime] NULL,
	[userId] [int] NULL,
	[brandId] [int] NOT NULL,
	[statusId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[carId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CarAdditionalFunction]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CarAdditionalFunction](
	[carId] [int] NOT NULL,
	[AdditionalFunctionId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[carId] ASC,
	[AdditionalFunctionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CarAddress]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CarAddress](
	[addressId] [int] IDENTITY(1,1) NOT NULL,
	[province] [nvarchar](50) NULL,
	[provinceId] [int] NULL,
	[district] [nvarchar](50) NULL,
	[districtId] [int] NULL,
	[ward] [nvarchar](50) NULL,
	[wardId] [int] NULL,
	[street] [nvarchar](255) NULL,
	[carId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[addressId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CarDraft]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CarDraft](
	[draftId] [int] IDENTITY(1,1) NOT NULL,
	[userId] [int] NOT NULL,
	[step] [int] NULL,
	[name] [nvarchar](100) NULL,
	[licensePlate] [nvarchar](20) NULL,
	[lastModified] [datetime] NULL,
	[model] [nvarchar](50) NULL,
	[color] [nvarchar](20) NULL,
	[seatNo] [int] NULL,
	[productionYear] [int] NULL,
	[transmission] [nvarchar](20) NULL,
	[fuel] [nvarchar](20) NULL,
	[mileage] [decimal](18, 2) NULL,
	[fuelConsumption] [decimal](18, 2) NULL,
	[additionalFunction] [nvarchar](max) NULL,
	[province] [nvarchar](50) NULL,
	[district] [nvarchar](50) NULL,
	[ward] [nvarchar](50) NULL,
	[street] [nvarchar](255) NULL,
	[basePrice] [decimal](18, 2) NULL,
	[deposit] [decimal](18, 2) NULL,
	[description] [nvarchar](max) NULL,
	[termOfUse] [nvarchar](max) NULL,
	[carPrice] [decimal](18, 2) NULL,
	[front] [nvarchar](200) NULL,
	[back] [nvarchar](200) NULL,
	[left] [nvarchar](200) NULL,
	[right] [nvarchar](200) NULL,
	[registration] [nvarchar](200) NULL,
	[certificate] [nvarchar](200) NULL,
	[insurance] [nvarchar](200) NULL,
	[brandId] [int] NULL,
	[carId] [int] NULL,
	[verifyStatus] [nvarchar](20) NULL,
PRIMARY KEY CLUSTERED 
(
	[draftId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CarStatus]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CarStatus](
	[CarStatusId] [int] NOT NULL,
	[name] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[CarStatusId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Feedback]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Feedback](
	[FeedbackId] [int] IDENTITY(1,1) NOT NULL,
	[bookingId] [int] NULL,
	[rating] [int] NULL,
	[content] [nvarchar](max) NULL,
	[dateTime] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[FeedbackId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Notification]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Notification](
	[notificationId] [int] IDENTITY(1,1) NOT NULL,
	[content] [nvarchar](max) NULL,
	[isRead] [bit] NULL,
	[userId] [int] NULL,
	[createAt] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[notificationId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PaymentMethod]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PaymentMethod](
	[PaymentMethodId] [int] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[PaymentMethodId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Role]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Role](
	[RoleId] [int] IDENTITY(1,1) NOT NULL,
	[roleName] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[RoleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Transaction]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Transaction](
	[transactionId] [int] IDENTITY(1,1) NOT NULL,
	[transactionType] [varchar](50) NOT NULL,
	[amount] [decimal](18, 2) NOT NULL,
	[transactionDate] [datetime] NULL,
	[userId] [int] NOT NULL,
	[bookingId] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[transactionId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[UserRole]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[UserRole](
	[userId] [int] NOT NULL,
	[roleId] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[userId] ASC,
	[roleId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[userId] [int] IDENTITY(1,1) NOT NULL,
	[username] [nvarchar](50) NULL,
	[avatar] [nvarchar](max) NULL,
	[dob] [date] NULL,
	[email] [nvarchar](100) NOT NULL,
	[nationalId] [nvarchar](20) NULL,
	[phone] [nvarchar](15) NOT NULL,
	[drivingLicense] [nvarchar](200) NULL,
	[wallet] [decimal](18, 2) NULL,
	[password] [nvarchar](255) NOT NULL,
	[city] [nvarchar](100) NULL,
	[district] [nvarchar](100) NULL,
	[ward] [nvarchar](100) NULL,
	[street] [nvarchar](255) NULL,
	[fullName] [nvarchar](100) NULL,
	[agreeTerms] [int] NOT NULL,
	[status] [varchar](10) NOT NULL,
	[salaryDriver] [decimal](18, 2) NULL,
	[descriptionDriver] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[userId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[VerificationToken]    Script Date: 12/25/2024 12:14:42 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VerificationToken](
	[tokenId] [int] IDENTITY(1,1) NOT NULL,
	[tokenCode] [nvarchar](max) NULL,
	[expiryDate] [datetime] NULL,
	[userId] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[tokenId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[AdditionalFunction] ON 
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (1, N'GPS')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (2, N'Child lock')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (3, N'Sun roof')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (4, N'DVD')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (5, N'Ski Rack')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (6, N'Car Cover')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (7, N'Car Wash')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (8, N'Car Wax')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (9, N'Car Polish')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (10, N'Car Vacuum')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (11, N'Car Freshener')
GO
INSERT [dbo].[AdditionalFunction] ([AdditionalFunctionId], [functionName]) VALUES (12, N'Car Shampoo')
GO
SET IDENTITY_INSERT [dbo].[AdditionalFunction] OFF
GO
SET IDENTITY_INSERT [dbo].[Booking] ON 
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (1, CAST(N'2024-10-12T20:30:00.000' AS DateTime), CAST(N'2024-10-14T20:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-10-14T19:44:33.387' AS DateTime), CAST(1958333.33 AS Decimal(18, 2)), 3, 5, 1, CAST(N'2024-10-14T19:44:33.403' AS DateTime), 4)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (2, CAST(N'2024-10-21T20:30:00.000' AS DateTime), CAST(N'2024-10-24T20:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-10-24T19:48:04.890' AS DateTime), CAST(5916666.67 AS Decimal(18, 2)), 3, 5, 1, CAST(N'2024-10-24T19:48:17.703' AS DateTime), 4)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (3, CAST(N'2024-11-03T20:30:00.000' AS DateTime), CAST(N'2024-11-04T20:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-11-04T19:52:28.987' AS DateTime), CAST(958333.33 AS Decimal(18, 2)), 3, 5, 1, CAST(N'2024-11-04T19:52:29.013' AS DateTime), NULL)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (5, CAST(N'2024-12-21T12:30:00.000' AS DateTime), CAST(N'2024-12-23T12:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-12-25T11:55:08.947' AS DateTime), CAST(8700000.00 AS Decimal(18, 2)), 3, 5, 1, CAST(N'2024-12-25T11:55:13.103' AS DateTime), 9)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (6, CAST(N'2024-12-20T12:30:00.000' AS DateTime), CAST(N'2024-12-23T12:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-12-28T12:30:00.000' AS DateTime), CAST(4500000.00 AS Decimal(18, 2)), 3, 3, 1, CAST(N'2024-12-25T11:59:42.837' AS DateTime), NULL)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (7, CAST(N'2024-12-24T12:30:00.000' AS DateTime), CAST(N'2024-12-26T12:30:00.000' AS DateTime), N'Taipt2', CAST(N'2024-12-27T12:30:00.000' AS DateTime), CAST(2800000.00 AS Decimal(18, 2)), 3, 3, 1, CAST(N'2024-12-25T12:00:55.760' AS DateTime), NULL)
GO
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [lastModified], [driverId]) VALUES (8, CAST(N'2024-12-25T12:00:00.000' AS DateTime), CAST(N'2024-12-25T17:00:00.000' AS DateTime), N'Taipt2', CAST(N'2024-12-25T18:00:00.000' AS DateTime), CAST(416666.67 AS Decimal(18, 2)), 3, 3, 1, CAST(N'2024-12-25T12:11:06.637' AS DateTime), NULL)
GO
SET IDENTITY_INSERT [dbo].[Booking] OFF
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (1, 1)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (1, 3)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (2, 2)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (2, 5)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (2, 8)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (3, 6)
GO
INSERT [dbo].[BookingCar] ([carId], [bookingId]) VALUES (10, 7)
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (1, N'Pending deposit')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (2, N'Confirmed')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (3, N'In-Progress')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (4, N'Pending payment')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (5, N'Completed')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (6, N'Cancelled')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (7, N'Pending cancel')
GO
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (8, N'Pending return')
GO
SET IDENTITY_INSERT [dbo].[Brand] ON 
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (1, N'Toyota')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (2, N'Honda')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (3, N'Hyundai')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (4, N'Kia')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (5, N'Mazda')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (6, N'Ford')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (7, N'Chevrolet')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (8, N'Mercedes-Benz')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (9, N'BMW')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (10, N'Audi')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (11, N'Lexus')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (12, N'Nissan')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (13, N'Volkswagen')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (14, N'Peugeot')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (15, N'Suzuki')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (16, N'Subaru')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (17, N'Mitsubishi')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (18, N'Volvo')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (19, N'Land Rover')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (20, N'Jeep')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (21, N'Porsche')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (22, N'Jaguar')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (23, N'Ferrari')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (24, N'Lamborghini')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (25, N'Rolls-Royce')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (26, N'Bentley')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (27, N'Bugatti')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (28, N'Maserati')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (29, N'McLaren')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (30, N'Aston Martin')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (31, N'Lotus')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (32, N'Alfa Romeo')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (33, N'Fiat')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (34, N'Citroen')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (35, N'Renault')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (36, N'Skoda')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (37, N'Seat')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (38, N'Opel')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (39, N'Dacia')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (40, N'Lada')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (41, N'ZAZ')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (42, N'GAZ')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (43, N'UAZ')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (44, N'Moskvich')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (45, N'Kamaz')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (46, N'PAZ')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (47, N'KAvZ')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (48, N'Ikarus')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (49, N'Neoplan')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (50, N'Setra')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (51, N'Van Hool')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (52, N'Volvo')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (53, N'Scania')
GO
INSERT [dbo].[Brand] ([BrandId], [brandName]) VALUES (54, N'Vinfast')
GO
SET IDENTITY_INSERT [dbo].[Brand] OFF
GO
SET IDENTITY_INSERT [dbo].[Car] ON 
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (1, N'Toyota INNOVA 2023', N'29A-12341', N'INNOVA', N'Red', 7, 2023, N'Automatic', N'Gasoline', CAST(1000.00 AS Decimal(18, 2)), CAST(7.00 AS Decimal(18, 2)), CAST(1000000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\1\frontImage.png', N'uploads\User\2\Car\1\backImage.png', N'uploads\User\2\Car\1\leftImage.png', N'uploads\User\2\Car\1\rightImage.png', N'uploads\User\2\Car\1\registration.png', N'uploads\User\2\Car\1\certificate.png', N'uploads\User\2\Car\1\insurance.png', CAST(N'2024-12-25T10:31:29.640' AS DateTime), 2, 1, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (2, N'Hyundai ACCENT  2023', N'29A-12345', N'ACCENT ', N'Red', 7, 2023, N'Manual', N'Gasoline', CAST(2000.00 AS Decimal(18, 2)), CAST(9.50 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), N'', N'', CAST(2000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\2\frontImage.png', N'uploads\User\2\Car\2\backImage.png', N'uploads\User\2\Car\2\leftImage.png', N'uploads\User\2\Car\2\rightImage.png', N'uploads\User\2\Car\2\registration.png', N'uploads\User\2\Car\2\certificate.png', N'uploads\User\2\Car\2\insurance.png', CAST(N'2024-12-25T12:11:01.757' AS DateTime), 2, 3, 2)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (3, N'Subaru XFORCE GLX 2024', N'29A-12345', N'XFORCE GLX', N'Black', 5, 2024, N'Automatic', N'Gasoline', CAST(2400.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(15000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\3\frontImage.png', N'uploads\User\2\Car\3\backImage.png', N'uploads\User\2\Car\3\leftImage.png', N'uploads\User\2\Car\3\rightImage.png', N'uploads\User\2\Car\3\registration.png', N'uploads\User\2\Car\3\certificate.png', N'uploads\User\2\Car\3\insurance.png', CAST(N'2024-12-25T11:58:55.410' AS DateTime), 2, 17, 2)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (4, N'Toyota WIGO 2022', N'29A-12345', N'WIGO', N'Grey', 5, 2022, N'Manual', N'Gasoline', CAST(1500.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), CAST(1000000.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\4\frontImage.png', N'uploads\User\2\Car\4\backImage.png', N'uploads\User\2\Car\4\leftImage.png', N'uploads\User\2\Car\4\rightImage.png', N'uploads\User\2\Car\4\registration.png', N'uploads\User\2\Car\4\certificate.png', N'uploads\User\2\Car\4\insurance.png', CAST(N'2024-12-25T10:26:13.260' AS DateTime), 2, 1, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (5, N'Ford RANGER XLS 4x2 2023', N'29A-12341', N'RANGER XLS 4x2', N'Red', 5, 2023, N'Automatic', N'Diesel', CAST(2500.00 AS Decimal(18, 2)), CAST(7.00 AS Decimal(18, 2)), CAST(1000000.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\5\frontImage.png', N'uploads\User\2\Car\5\backImage.png', N'uploads\User\2\Car\5\leftImage.png', N'uploads\User\2\Car\5\rightImage.png', N'uploads\User\2\Car\5\registration.png', N'uploads\User\2\Car\5\certificate.png', N'uploads\User\2\Car\5\insurance.png', CAST(N'2024-12-25T10:50:37.533' AS DateTime), 2, 6, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (6, N'Vinfast FADIL 2022', N'29A-12342', N'FADIL', N'Silver', 5, 2022, N'Automatic', N'Gasoline', CAST(3000.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), CAST(500000.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), N'', N'', CAST(500000.00 AS Decimal(18, 2)), N'uploads\User\8\Car\6\frontImage.png', N'uploads\User\8\Car\6\backImage.png', N'uploads\User\8\Car\6\leftImage.png', N'uploads\User\8\Car\6\rightImage.png', N'uploads\User\8\Car\6\registration.png', N'uploads\User\8\Car\6\certificate.png', N'uploads\User\8\Car\6\insurance.png', CAST(N'2024-12-25T11:11:39.003' AS DateTime), 8, 54, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (7, N'Vinfast LUX A PREMIUM 2022', N'29A-12342', N'LUX A PREMIUM', N'Grey', 4, 2022, N'Automatic', N'Gasoline', CAST(1200.00 AS Decimal(18, 2)), CAST(8.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(1200000.00 AS Decimal(18, 2)), N'uploads\User\8\Car\7\frontImage.png', N'uploads\User\8\Car\7\backImage.png', N'uploads\User\8\Car\7\leftImage.png', N'uploads\User\8\Car\7\rightImage.png', N'uploads\User\8\Car\7\registration.png', N'uploads\User\8\Car\7\certificate.png', N'uploads\User\8\Car\7\insurance.png', CAST(N'2024-12-25T11:15:26.817' AS DateTime), 8, 54, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (8, N'Honda CITY 2024', N'29A-12342', N'CITY', N'Silver', 4, 2024, N'Automatic', N'Gasoline', CAST(2000.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), CAST(800000.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), N'', N'', CAST(800000.00 AS Decimal(18, 2)), N'uploads\User\8\Car\8\frontImage.png', N'uploads\User\8\Car\8\backImage.png', N'uploads\User\8\Car\8\leftImage.png', N'uploads\User\8\Car\8\rightImage.png', N'uploads\User\8\Car\8\registration.png', N'uploads\User\8\Car\8\certificate.png', N'uploads\User\8\Car\8\insurance.png', CAST(N'2024-12-25T11:19:08.027' AS DateTime), 8, 2, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (9, N'Nissan ALMERA 2024', N'29A-12342', N'ALMERA', N'Silver', 5, 2024, N'Automatic', N'Gasoline', CAST(1800.00 AS Decimal(18, 2)), CAST(5.00 AS Decimal(18, 2)), CAST(1100000.00 AS Decimal(18, 2)), CAST(2200000.00 AS Decimal(18, 2)), N'', N'', CAST(1100000.00 AS Decimal(18, 2)), N'uploads\User\8\Car\9\frontImage.png', N'uploads\User\8\Car\9\backImage.png', N'uploads\User\8\Car\9\leftImage.png', N'uploads\User\8\Car\9\rightImage.png', N'uploads\User\8\Car\9\registration.png', N'uploads\User\8\Car\9\certificate.png', N'uploads\User\8\Car\9\insurance.png', CAST(N'2024-12-25T11:23:17.347' AS DateTime), 8, 12, 1)
GO
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (10, N'Nissan NAVARA 4x2 2023', N'29A-12346', N'NAVARA 4x2', N'Black', 5, 2023, N'Automatic', N'Diesel', CAST(2200.00 AS Decimal(18, 2)), CAST(7.00 AS Decimal(18, 2)), CAST(1400000.00 AS Decimal(18, 2)), CAST(2300000.00 AS Decimal(18, 2)), N'', N'', CAST(1400000.00 AS Decimal(18, 2)), N'uploads\User\8\Car\10\frontImage.png', N'uploads\User\8\Car\10\backImage.png', N'uploads\User\8\Car\10\leftImage.png', N'uploads\User\8\Car\10\rightImage.png', N'uploads\User\8\Car\10\registration.png', N'uploads\User\8\Car\10\certificate.png', N'uploads\User\8\Car\10\insurance.png', CAST(N'2024-12-25T12:00:28.567' AS DateTime), 8, 12, 2)
GO
SET IDENTITY_INSERT [dbo].[Car] OFF
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 3)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (1, 7)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (2, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (2, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (2, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (2, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (3, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (3, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (3, 9)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (3, 11)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (4, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (4, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (4, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 7)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 9)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (5, 10)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 10)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (6, 11)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (7, 7)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (8, 7)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (9, 7)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 1)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 2)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 4)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 5)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 6)
GO
INSERT [dbo].[CarAdditionalFunction] ([carId], [AdditionalFunctionId]) VALUES (10, 7)
GO
SET IDENTITY_INSERT [dbo].[CarAddress] ON 
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (1, N'Thành phố Hà Nội', 1, N'Quận Hoàn Kiếm', 2, N'Phường Đồng Xuân', 40, N'', 1)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (2, N'Thành phố Hà Nội', 1, N'Quận Ba Đình', 1, N'Phường Ngọc Hà', 16, N'', 2)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (3, N'Thành phố Hà Nội', 1, N'Quận Ba Đình', 1, N'Phường Trúc Bạch', 4, N'', 3)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (4, N'Tỉnh Cao Bằng', 4, N'Huyện Bảo Lâm', 42, N'Xã Lý Bôn', 1294, N'', 4)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (5, N'Thành phố Hà Nội', 1, N'Quận Hoàng Mai', 8, N'Phường Thanh Trì', 301, N'', 5)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (6, N'Thành phố Hà Nội', 1, N'Quận Bắc Từ Liêm', 21, N'Phường Liên Mạc', 598, N'', 6)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (7, N'Thành phố Hà Nội', 1, N'Huyện Thanh Trì', 20, N'Xã Thanh Liệt', 646, N'', 7)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (8, N'Thành phố Hà Nội', 1, N'Quận Thanh Xuân', 9, N'Phường Thượng Đình', 346, N'', 8)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (9, N'Thành phố Hà Nội', 1, N'Quận Tây Hồ', 3, N'Phường Nhật Tân', 94, N'', 9)
GO
INSERT [dbo].[CarAddress] ([addressId], [province], [provinceId], [district], [districtId], [ward], [wardId], [street], [carId]) VALUES (10, N'Thành phố Hà Nội', 1, N'Quận Hoàn Kiếm', 2, N'Phường Đồng Xuân', 40, N'', 10)
GO
SET IDENTITY_INSERT [dbo].[CarAddress] OFF
GO
SET IDENTITY_INSERT [dbo].[CarDraft] ON 
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (2, 2, NULL, N'Toyota XAW 2022', N'29A-12345', CAST(N'2024-12-25T05:41:34.657' AS DateTime), N'XAW', N'Red', 7, 2022, N'Manual', N'Diesel', CAST(1500.00 AS Decimal(18, 2)), CAST(9.00 AS Decimal(18, 2)), N'6,2,5,', N'4,Tỉnh Cao Bằng', N'42,Huyện Bảo Lâm', N'1294,Xã Lý Bôn', N'', CAST(1000000.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\4\frontImage.png', N'uploads\User\2\Car\4\backImage.png', N'uploads\User\2\Car\4\leftImage.png', N'uploads\User\2\Car\4\rightImage.png', N'uploads\User\2\Car\4\registration.png', N'uploads\User\2\Car\4\certificate.png', N'uploads\User\2\Car\4\insurance.png', 1, 4, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (3, 2, NULL, N'Toyota XAW 2023', N'29A-12345', CAST(N'2024-12-25T05:50:58.920' AS DateTime), N'XAW', N'Red', 7, 2023, N'Manual', N'Diesel', CAST(1500.00 AS Decimal(18, 2)), CAST(9.00 AS Decimal(18, 2)), N'6,5,2,', N'4,Tỉnh Cao Bằng', N'42,Huyện Bảo Lâm', N'1294,Xã Lý Bôn', N'', CAST(1000000.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\4\frontImage.png', N'uploads\User\2\Car\4\backImage.png', N'uploads\User\2\Car\4\leftImage.png', N'uploads\User\2\Car\4\rightImage.png', N'uploads\User\2\Car\4\registration.png', N'uploads\User\2\Car\4\certificate.png', N'uploads\User\2\Car\4\insurance.png', 1, 4, N'Rejected')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (4, 2, NULL, N'Subaru XFORCE GLX 2024', N'29A-12345', CAST(N'2024-12-25T10:16:34.037' AS DateTime), N'XFORCE GLX', N'Black', 5, 2024, N'Automatic', N'Gasoline', CAST(2400.00 AS Decimal(18, 2)), CAST(11.00 AS Decimal(18, 2)), N'1,5,', N'1,Thành phố Hà Nội', N'1,Quận Ba Đình', N'4,Phường Trúc Bạch', N'', CAST(1500000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(15000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\3\frontImage.png', N'uploads\User\2\Car\3\backImage.png', N'uploads\User\2\Car\3\leftImage.png', N'uploads\User\2\Car\3\rightImage.png', N'uploads\User\2\Draft\4\registration.png', N'uploads\User\2\Draft\4\certificate.png', N'uploads\User\2\Draft\4\insurance.png', 16, 3, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (5, 2, NULL, N'Subaru XFORCE GLX 2024', N'29A-12345', CAST(N'2024-12-25T10:17:17.617' AS DateTime), N'XFORCE GLX', N'Black', 5, 2024, N'Automatic', N'Gasoline', CAST(2400.00 AS Decimal(18, 2)), CAST(11.00 AS Decimal(18, 2)), N'5,1,', N'1,Thành phố Hà Nội', N'1,Quận Ba Đình', N'4,Phường Trúc Bạch', N'', CAST(1500000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(15000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\3\frontImage.png', N'uploads\User\2\Car\3\backImage.png', N'uploads\User\2\Car\3\leftImage.png', N'uploads\User\2\Car\3\rightImage.png', N'uploads\User\2\Car\3\registration.png', N'uploads\User\2\Car\3\certificate.png', N'uploads\User\2\Car\3\insurance.png', 16, 3, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (6, 2, NULL, N'Toyota WIGO 2022', N'29A-12345', CAST(N'2024-12-25T10:23:32.480' AS DateTime), N'WIGO', N'Grey', 5, 2022, N'Manual', N'Gasoline', CAST(1500.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), N'2,6,5,', N'4,Tỉnh Cao Bằng', N'42,Huyện Bảo Lâm', N'1294,Xã Lý Bôn', N'', CAST(1000000.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\4\frontImage.png', N'uploads\User\2\Car\4\backImage.png', N'uploads\User\2\Car\4\leftImage.png', N'uploads\User\2\Car\4\rightImage.png', N'uploads\User\2\Car\4\registration.png', N'uploads\User\2\Car\4\certificate.png', N'uploads\User\2\Car\4\insurance.png', 1, 4, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (7, 2, NULL, N'Toyota WIGO 2022', N'29A-12345', CAST(N'2024-12-25T10:24:39.547' AS DateTime), N'WIGO', N'Grey', 5, 2022, N'Manual', N'Gasoline', CAST(1500.00 AS Decimal(18, 2)), CAST(6.00 AS Decimal(18, 2)), N'2,5,6,', N'4,Tỉnh Cao Bằng', N'42,Huyện Bảo Lâm', N'1294,Xã Lý Bôn', N'', CAST(1000000.00 AS Decimal(18, 2)), CAST(1200000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\4\frontImage.png', N'uploads\User\2\Car\4\backImage.png', N'uploads\User\2\Car\4\leftImage.png', N'uploads\User\2\Car\4\rightImage.png', N'uploads\User\2\Draft\7\registration.png', N'uploads\User\2\Draft\7\certificate.png', N'uploads\User\2\Draft\7\insurance.png', 1, 4, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (8, 2, NULL, N'Toyota INNOVA 2023', N'29A-12341', CAST(N'2024-12-25T10:29:19.163' AS DateTime), N'INNOVA', N'Red', 7, 2023, N'Automatic', N'Gasoline', CAST(1000.00 AS Decimal(18, 2)), CAST(7.00 AS Decimal(18, 2)), N'5,6,7,2,1,3,', N'1,Thành phố Hà Nội', N'2,Quận Hoàn Kiếm', N'40,Phường Đồng Xuân', N'', CAST(1000000.00 AS Decimal(18, 2)), CAST(2000000.00 AS Decimal(18, 2)), N'', N'', CAST(1000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\1\frontImage.png', N'uploads\User\2\Car\1\backImage.png', N'uploads\User\2\Car\1\leftImage.png', N'uploads\User\2\Car\1\rightImage.png', N'uploads\User\2\Draft\8\registration.png', N'uploads\User\2\Draft\8\certificate.png', N'uploads\User\2\Draft\8\insurance.png', 1, 1, N'Verified')
GO
INSERT [dbo].[CarDraft] ([draftId], [userId], [step], [name], [licensePlate], [lastModified], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [additionalFunction], [province], [district], [ward], [street], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [brandId], [carId], [verifyStatus]) VALUES (9, 2, NULL, N'Subaru X 2022', N'29A-12345', CAST(N'2024-12-25T10:31:52.853' AS DateTime), N'X', N'Green', 7, 2022, N'Manual', N'Gasoline', CAST(2000.00 AS Decimal(18, 2)), CAST(9.50 AS Decimal(18, 2)), N'2,6,1,5,', N'1,Thành phố Hà Nội', N'1,Quận Ba Đình', N'16,Phường Ngọc Hà', N'', CAST(2000000.00 AS Decimal(18, 2)), CAST(1500000.00 AS Decimal(18, 2)), N'', N'', CAST(2000000.00 AS Decimal(18, 2)), N'uploads\User\2\Car\2\frontImage.png', N'uploads\User\2\Car\2\backImage.png', N'uploads\User\2\Car\2\leftImage.png', N'uploads\User\2\Car\2\rightImage.png', N'uploads\User\2\Draft\9\registration.png', N'uploads\User\2\Draft\9\certificate.png', N'uploads\User\2\Draft\9\insurance.png', 16, 2, N'Verified')
GO
SET IDENTITY_INSERT [dbo].[CarDraft] OFF
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (1, N'Available')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (2, N'Booked')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (3, N'Stopped')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (4, N'Deleted')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (5, N'Maintenance')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (6, N'Rented')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (7, N'Returned')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (8, N'Verifying')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (9, N'Confirmed')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (10, N'In-Progress')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (11, N'Pending payment')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (12, N'Completed')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (13, N'Cancelled')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (14, N'Pending deposit')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (15, N'Pending cancel')
GO
INSERT [dbo].[CarStatus] ([CarStatusId], [name]) VALUES (16, N'Pending return')
GO
SET IDENTITY_INSERT [dbo].[Feedback] ON 
GO
INSERT [dbo].[Feedback] ([FeedbackId], [bookingId], [rating], [content], [dateTime]) VALUES (1, 1, 5, N'oke ', CAST(N'2024-12-24T19:44:42.460' AS DateTime))
GO
INSERT [dbo].[Feedback] ([FeedbackId], [bookingId], [rating], [content], [dateTime]) VALUES (2, 3, 5, N'', CAST(N'2024-12-24T19:52:33.750' AS DateTime))
GO
INSERT [dbo].[Feedback] ([FeedbackId], [bookingId], [rating], [content], [dateTime]) VALUES (3, 5, 5, N'oke ', CAST(N'2024-12-25T11:55:24.180' AS DateTime))
GO
SET IDENTITY_INSERT [dbo].[Feedback] OFF
GO
SET IDENTITY_INSERT [dbo].[PaymentMethod] ON 
GO
INSERT [dbo].[PaymentMethod] ([PaymentMethodId], [name]) VALUES (1, N'Wallet')
GO
INSERT [dbo].[PaymentMethod] ([PaymentMethodId], [name]) VALUES (2, N'VNPay')
GO
INSERT [dbo].[PaymentMethod] ([PaymentMethodId], [name]) VALUES (3, N'Money')
GO
INSERT [dbo].[PaymentMethod] ([PaymentMethodId], [name]) VALUES (4, N'MOMO')
GO
SET IDENTITY_INSERT [dbo].[PaymentMethod] OFF
GO
SET IDENTITY_INSERT [dbo].[Role] ON 
GO
INSERT [dbo].[Role] ([RoleId], [roleName]) VALUES (1, N'Admin')
GO
INSERT [dbo].[Role] ([RoleId], [roleName]) VALUES (2, N'Car Owner')
GO
INSERT [dbo].[Role] ([RoleId], [roleName]) VALUES (3, N'Customer')
GO
INSERT [dbo].[Role] ([RoleId], [roleName]) VALUES (4, N'Driver')
GO
SET IDENTITY_INSERT [dbo].[Role] OFF
GO
SET IDENTITY_INSERT [dbo].[Transaction] ON 
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (1, N'Top-up', CAST(10000000.00 AS Decimal(18, 2)), CAST(N'2024-10-12T19:40:14.643' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (2, N'Pay deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-10-12T19:42:53.913' AS DateTime), 3, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (3, N'Receive deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-10-12T19:42:53.923' AS DateTime), 2, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (4, N'Receive salary', CAST(977208.33 AS Decimal(18, 2)), CAST(N'2024-10-14T19:44:27.100' AS DateTime), 4, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (5, N'Pay for driver rental', CAST(977208.33 AS Decimal(18, 2)), CAST(N'2024-12-24T19:44:27.103' AS DateTime), 3, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (6, N'Receive remaining deposit', CAST(41666.67 AS Decimal(18, 2)), CAST(N'2024-10-14T19:44:33.390' AS DateTime), 3, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (7, N'Return remaining deposit', CAST(41666.67 AS Decimal(18, 2)), CAST(N'2024-10-14T19:44:33.397' AS DateTime), 2, 1)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (8, N'Pay deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-10-21T19:46:34.170' AS DateTime), 3, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (9, N'Receive deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-10-21T19:46:34.177' AS DateTime), 2, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (10, N'Top-up', CAST(5000000.00 AS Decimal(18, 2)), CAST(N'2024-10-21T19:46:44.843' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (11, N'Receive salary', CAST(1476208.33 AS Decimal(18, 2)), CAST(N'2024-10-24T19:47:57.717' AS DateTime), 4, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (12, N'Pay for driver rental', CAST(1476208.33 AS Decimal(18, 2)), CAST(N'2024-10-24T19:47:57.720' AS DateTime), 3, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (13, N'Receive final payment', CAST(4416666.67 AS Decimal(18, 2)), CAST(N'2024-10-24T19:48:17.693' AS DateTime), 2, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (14, N'Pay final payment', CAST(4416666.67 AS Decimal(18, 2)), CAST(N'2024-10-24T19:48:17.697' AS DateTime), 3, 2)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (15, N'Pay deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-11-03T19:48:33.023' AS DateTime), 3, 3)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (16, N'Receive deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-11-03T19:48:33.030' AS DateTime), 2, 3)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (17, N'Receive remaining deposit', CAST(1041666.67 AS Decimal(18, 2)), CAST(N'2024-11-04T19:52:28.993' AS DateTime), 3, 3)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (18, N'Return remaining deposit', CAST(1041666.67 AS Decimal(18, 2)), CAST(N'2024-11-04T19:52:29.003' AS DateTime), 2, 3)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (23, N'Withdraw', CAST(3713250.00 AS Decimal(18, 2)), CAST(N'2024-12-24T20:34:18.153' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (24, N'Top-up', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-12-24T20:34:20.677' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (25, N'Top-up', CAST(10000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:52:19.647' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (26, N'Top-up', CAST(10000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:52:24.047' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (27, N'Pay deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:53:50.343' AS DateTime), 3, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (28, N'Receive deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:53:50.350' AS DateTime), 2, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (29, N'Receive salary', CAST(1975208.33 AS Decimal(18, 2)), CAST(N'2024-12-25T11:55:01.050' AS DateTime), 9, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (30, N'Pay for driver rental', CAST(1975208.33 AS Decimal(18, 2)), CAST(N'2024-12-25T11:55:01.053' AS DateTime), 3, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (31, N'Receive final payment', CAST(7200000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:55:13.093' AS DateTime), 2, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (32, N'Pay final payment', CAST(7200000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:55:13.100' AS DateTime), 3, 5)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (33, N'Top-up', CAST(10000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:56:38.717' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (34, N'Pay deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:57:08.370' AS DateTime), 3, 6)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (35, N'Receive deposit', CAST(2000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:57:08.370' AS DateTime), 2, 6)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (36, N'Top-up', CAST(10000000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:57:56.353' AS DateTime), 3, NULL)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (37, N'Pay deposit', CAST(2300000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:58:42.150' AS DateTime), 3, 7)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (38, N'Receive deposit', CAST(2300000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T11:58:42.157' AS DateTime), 8, 7)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (39, N'Pay deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T12:10:54.310' AS DateTime), 3, 8)
GO
INSERT [dbo].[Transaction] ([transactionId], [transactionType], [amount], [transactionDate], [userId], [bookingId]) VALUES (40, N'Receive deposit', CAST(1500000.00 AS Decimal(18, 2)), CAST(N'2024-12-25T12:10:54.317' AS DateTime), 2, 8)
GO
SET IDENTITY_INSERT [dbo].[Transaction] OFF
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (1, 1)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (2, 2)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (3, 3)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (4, 4)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (7, 3)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (8, 2)
GO
INSERT [dbo].[UserRole] ([userId], [roleId]) VALUES (9, 4)
GO
SET IDENTITY_INSERT [dbo].[Users] ON 
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (1, N'admin', NULL, CAST(N'1990-01-15' AS Date), N'admin@gmail.com', N'123456789', N'0123456789', N'DL123456', CAST(9999999999.00 AS Decimal(18, 2)), N'$2a$10$zTJMk41R7yUiRWED31NbtueNZTaIOV8mJm4HGavw2KIZVmCKgA8MW', N'1', N'8', N'334', N'Cau Giay', N'Admin', 1, N'ACTIVATED', NULL, NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (2, N'taipt', N'uploads\User\2\avatar.png', CAST(N'2003-08-20' AS Date), N'pt0832a@gmail.com', N'123123123123', N'+84911111111', N'uploads\User\2\drivingLicense.png', CAST(21033333.33 AS Decimal(18, 2)), N'$2a$10$ZwDQHarGoQmgVxe3ebfa0eYQHt6bcpigW60sCtmbTzBcOT7l5Q3Za', N'1', N'3', N'91', N'home 1', N'taipt', 1, N'ACTIVATED', NULL, NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (3, N'taipt2', N'uploads\User\3\avatar.png', CAST(N'2003-08-20' AS Date), N'taipthe176793@fpt.edu.vn', N'123123123122', N'+84911111112', N'uploads\User\3\drivingLicense.png', CAST(25524791.68 AS Decimal(18, 2)), N'$2a$10$.Y9oIii4wd1sZp1fAmh/su.guMAK0Y.Ke6ifBE6MEq6i4.YlLiVT2', N'1', N'2', N'40', N'home 1', N'Taipt2', 1, N'ACTIVATED', NULL, NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (4, N'john doe', N'uploads\User\4\avatar.png', CAST(N'1990-12-04' AS Date), N'jd218274182@gmail.com', N'111222333444', N'+84999999999', N'uploads\User\4\drivingLicense.png', CAST(2453416.66 AS Decimal(18, 2)), N'$2a$10$bsov/YsxeL7CM6Ww0KDmFeXFlMWk4rvMArFSszNmfGJiY0djTAb26', N'1', N'3', N'94', N'', N'john doe', 0, N'ACTIVATED', CAST(499000.00 AS Decimal(18, 2)), NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (7, N'customer2', N'uploads\User\7\avatar.png', CAST(N'1999-12-06' AS Date), N'customer2@mssg.com', N'111222333555', N'+84923123123', N'uploads\User\7\drivingLicense.png', CAST(0.00 AS Decimal(18, 2)), N'$2a$10$nfkIFxTg16ThWhWMAPZ9dOAViO4PXMzpMKHHjgxUkaA8NBr5Yl/pG', N'1', N'2', N'40', N'', N'Phan Tuan Duy', 0, N'ACTIVATED', NULL, NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (8, N'carowner2', N'uploads\User\8\avatar.png', CAST(N'1990-05-08' AS Date), N'carowner2@mssg.com', N'111222333553', N'+84911122233', N'uploads\User\8\drivingLicense.png', CAST(2300000.00 AS Decimal(18, 2)), N'$2a$10$FasDbBZFcu5qAGrFc3obYeJktaIn6/IECeHS95svm4lBB0NgmcEt.', N'1', N'3', N'91', N'', N'Tran Huy Chuong', 0, N'ACTIVATED', NULL, NULL)
GO
INSERT [dbo].[Users] ([userId], [username], [avatar], [dob], [email], [nationalId], [phone], [drivingLicense], [wallet], [password], [city], [district], [ward], [street], [fullName], [agreeTerms], [status], [salaryDriver], [descriptionDriver]) VALUES (9, N'driver2', N'uploads\User\9\avatar.png', CAST(N'2001-04-17' AS Date), N'driver2@mssg.com', N'123123123125', N'+84898989532', N'uploads\User\9\drivingLicense.png', CAST(1975208.33 AS Decimal(18, 2)), N'$2a$10$oGlSCPBLQvzpEaWUXYzyIOIibVztEZYuN9F3ZaavaQCXp3UJzBPPy', N'1', N'2', N'40', N'', N'Ngo Quang Tam', 0, N'ACTIVATED', CAST(499000.00 AS Decimal(18, 2)), NULL)
GO
SET IDENTITY_INSERT [dbo].[Users] OFF
GO
SET IDENTITY_INSERT [dbo].[VerificationToken] ON 
GO
INSERT [dbo].[VerificationToken] ([tokenId], [tokenCode], [expiryDate], [userId]) VALUES (1, N'759c5b89-19f2-4878-b9a3-5374ec722c51', CAST(N'2024-12-25T19:38:02.150' AS DateTime), 2)
GO
INSERT [dbo].[VerificationToken] ([tokenId], [tokenCode], [expiryDate], [userId]) VALUES (2, N'dd84c456-7169-4668-ac1d-80e3fafadce5', CAST(N'2024-12-25T19:38:16.170' AS DateTime), 3)
GO
SET IDENTITY_INSERT [dbo].[VerificationToken] OFF
GO
/****** Object:  Index [UQ__CarAddre__1436F17521CB10E8]    Script Date: 12/25/2024 12:14:42 PM ******/
ALTER TABLE [dbo].[CarAddress] ADD UNIQUE NONCLUSTERED 
(
	[carId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__Feedback__C6D03BCCE83688BD]    Script Date: 12/25/2024 12:14:42 PM ******/
ALTER TABLE [dbo].[Feedback] ADD UNIQUE NONCLUSTERED 
(
	[bookingId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__Verifica__CB9A1CFEA7F277B5]    Script Date: 12/25/2024 12:14:42 PM ******/
ALTER TABLE [dbo].[VerificationToken] ADD UNIQUE NONCLUSTERED 
(
	[userId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Booking] ADD  DEFAULT (getdate()) FOR [lastModified]
GO
ALTER TABLE [dbo].[Car] ADD  DEFAULT (getdate()) FOR [lastModified]
GO
ALTER TABLE [dbo].[CarDraft] ADD  DEFAULT (getdate()) FOR [lastModified]
GO
ALTER TABLE [dbo].[Transaction] ADD  DEFAULT (getdate()) FOR [transactionDate]
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([bookingStatusId])
REFERENCES [dbo].[BookingStatus] ([BookingStatusId])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([driverId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([paymentMethodId])
REFERENCES [dbo].[PaymentMethod] ([PaymentMethodId])
GO
ALTER TABLE [dbo].[Booking]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[BookingCar]  WITH CHECK ADD FOREIGN KEY([bookingId])
REFERENCES [dbo].[Booking] ([bookingId])
GO
ALTER TABLE [dbo].[BookingCar]  WITH CHECK ADD FOREIGN KEY([carId])
REFERENCES [dbo].[Car] ([carId])
GO
ALTER TABLE [dbo].[Car]  WITH CHECK ADD FOREIGN KEY([brandId])
REFERENCES [dbo].[Brand] ([BrandId])
GO
ALTER TABLE [dbo].[Car]  WITH CHECK ADD FOREIGN KEY([statusId])
REFERENCES [dbo].[CarStatus] ([CarStatusId])
GO
ALTER TABLE [dbo].[Car]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[CarAdditionalFunction]  WITH CHECK ADD FOREIGN KEY([AdditionalFunctionId])
REFERENCES [dbo].[AdditionalFunction] ([AdditionalFunctionId])
GO
ALTER TABLE [dbo].[CarAdditionalFunction]  WITH CHECK ADD FOREIGN KEY([carId])
REFERENCES [dbo].[Car] ([carId])
GO
ALTER TABLE [dbo].[CarAddress]  WITH CHECK ADD FOREIGN KEY([carId])
REFERENCES [dbo].[Car] ([carId])
GO
ALTER TABLE [dbo].[CarDraft]  WITH CHECK ADD FOREIGN KEY([brandId])
REFERENCES [dbo].[Brand] ([BrandId])
GO
ALTER TABLE [dbo].[CarDraft]  WITH CHECK ADD FOREIGN KEY([carId])
REFERENCES [dbo].[Car] ([carId])
GO
ALTER TABLE [dbo].[CarDraft]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD FOREIGN KEY([bookingId])
REFERENCES [dbo].[Booking] ([bookingId])
GO
ALTER TABLE [dbo].[Notification]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[Transaction]  WITH CHECK ADD FOREIGN KEY([bookingId])
REFERENCES [dbo].[Booking] ([bookingId])
GO
ALTER TABLE [dbo].[Transaction]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[UserRole]  WITH CHECK ADD FOREIGN KEY([roleId])
REFERENCES [dbo].[Role] ([RoleId])
GO
ALTER TABLE [dbo].[UserRole]  WITH CHECK ADD FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[VerificationToken]  WITH CHECK ADD  CONSTRAINT [FK_UserToken] FOREIGN KEY([userId])
REFERENCES [dbo].[Users] ([userId])
GO
ALTER TABLE [dbo].[VerificationToken] CHECK CONSTRAINT [FK_UserToken]
GO
ALTER TABLE [dbo].[CarDraft]  WITH CHECK ADD CHECK  (([verifyStatus]='Cancelled' OR [verifyStatus]='Pending' OR [verifyStatus]='Rejected' OR [verifyStatus]='Verified'))
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD CHECK  (([rating]>=(1) AND [rating]<=(5)))
GO
ALTER TABLE [dbo].[Transaction]  WITH CHECK ADD CHECK  (([transactionType]='Receive salary' OR [transactionType]='Pay for driver rental' OR [transactionType]='Return remaining deposit' OR [transactionType]='Receive remaining deposit' OR [transactionType]='Receive final payment' OR [transactionType]='Pay final payment' OR [transactionType]='Offset final payment' OR [transactionType]='Refund deposit' OR [transactionType]='Receive deposit' OR [transactionType]='Pay deposit' OR [transactionType]='Top-up' OR [transactionType]='Withdraw'))
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD CHECK  (([status]='RENTED' OR [status]='DELETED' OR [status]='LOCKED' OR [status]='ACTIVATED' OR [status]='PENDING'))
GO
USE [master]
GO
ALTER DATABASE [RentalCar] SET  READ_WRITE 
GO
