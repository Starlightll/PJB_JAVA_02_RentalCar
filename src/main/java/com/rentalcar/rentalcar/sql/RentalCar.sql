CREATE DATABASE RentalCar
GO
USE RentalCar
GO


CREATE TABLE statusDriver (
                              statusDriverId INT  IDENTITY (1,1) PRIMARY KEY NOT NULL,
                              statusDriverName nvarchar(50)
)

-- Users table
CREATE TABLE [dbo].[Users]
(
    userId         INT IDENTITY (1,1) NOT NULL,
    username       NVARCHAR(50),
    dob            DATE,
    email          NVARCHAR(100)      NOT NULL,
    nationalId     NVARCHAR(20),
    phone          NVARCHAR(15)       NOT NULL,
    drivingLicense NVARCHAR(50),
    wallet         DECIMAL(18, 2),
    password       NVARCHAR(255)      NOT NULL,
    city           NVARCHAR(100),
    district       NVARCHAR(100),
    ward           NVARCHAR(100),
    street         NVARCHAR(255),
    fullName       NVARCHAR(100),
    agreeTerms     int                not null,
    status         VARCHAR(10)        NOT NULL CHECK (status IN ('PENDING', 'ACTIVATED', 'LOCKED', 'DELETED')),
    statusDriverId INT foreign key REFERENCES statusDriver(statusDriverId),
    PRIMARY KEY (userId)

    );

INSERT INTO statusDriver (statusDriverName)
VALUES
    ('Available'),  -- Available
    (N'Rented'),      -- Rented
    (N'Maintenance');    -- Maintenance

CREATE TABLE VerificationToken
(
    tokenId    INT IDENTITY (1,1) PRIMARY KEY,
    tokenCode  NVARCHAR(MAX),
    expiryDate DATETIME,
    userId     INT UNIQUE, -- Đảm bảo mỗi userId chỉ xuất hiện một lần
    CONSTRAINT FK_UserToken FOREIGN KEY (userId) REFERENCES users (userId)
);

-- Car Status table
CREATE TABLE [dbo].[CarStatus]
(
    CarStatusId INT IDENTITY (1,1) PRIMARY KEY,
    name        NVARCHAR(50),
    );


-- Brand table
CREATE TABLE [dbo].[Brand]
(
    BrandId   INT IDENTITY (1,1) PRIMARY KEY,
    brandName NVARCHAR(50),
    );




-- Car table
CREATE TABLE [dbo].[Car]
(
    carId           INT IDENTITY (1,1) PRIMARY KEY not null,
    name            NVARCHAR(100),
    licensePlate    NVARCHAR(20),
    model           NVARCHAR(50),
    color           NVARCHAR(20),
    seatNo          INT,
    productionYear  INT,
    transmission    NVARCHAR(20),
    fuel            NVARCHAR(20),
    mileage         DECIMAL(18, 2),
    fuelConsumption DECIMAL(18, 2),
    basePrice       DECIMAL(18, 2),
    deposit         DECIMAL(18, 2),
    description     NVARCHAR(MAX),
    termOfUse       NVARCHAR(MAX),
    carPrice        DECIMAL(18, 2),
    front           NVARCHAR(200),
    back            NVARCHAR(200),
    [left]          NVARCHAR(200),
    [right]         NVARCHAR(200),
    registration    NVARCHAR(200),
    certificate     NVARCHAR(200),
    insurance       NVARCHAR(200),
    lastModified    DATETIME DEFAULT CURRENT_TIMESTAMP,
    userId          INT,
    brandId         INT                            NOT NULL,
    statusId        INT                            NOT NULL,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (brandId) REFERENCES [dbo].[Brand] (brandId),
    FOREIGN KEY (statusId) REFERENCES [dbo].[CarStatus] (CarStatusId)
    );

-- Car Address table
CREATE TABLE [dbo].[CarAddress]
(
    addressId INT IDENTITY (1,1) PRIMARY KEY,
    province  NVARCHAR(50),
    provinceId INT,
    district  NVARCHAR(50),
    districtId INT,
    ward      NVARCHAR(50),
    wardId    INT,
    street    NVARCHAR(255),
    carId     INT UNIQUE NOT NULL,
    FOREIGN KEY (carId) REFERENCES [dbo].[Car] (carId)
    );

CREATE TABLE CarDraft
(
    draftId      INT IDENTITY (1,1) PRIMARY KEY not null,
    userId       INT NOT NULL,
    step         INT,
    name            NVARCHAR(100),
    licensePlate NVARCHAR(20),
    lastModified DATETIME DEFAULT CURRENT_TIMESTAMP,
    model           NVARCHAR(50),
    color           NVARCHAR(20),
    seatNo          INT,
    productionYear  INT,
    transmission    NVARCHAR(20),
    fuel            NVARCHAR(20),
    mileage         DECIMAL(18, 2),
    fuelConsumption DECIMAL(18, 2),
    additionalFunction NVARCHAR(MAX),
    province        NVARCHAR(50),
    district        NVARCHAR(50),
    ward            NVARCHAR(50),
    street          NVARCHAR(255),
    basePrice       DECIMAL(18, 2),
    deposit         DECIMAL(18, 2),
    description     NVARCHAR(MAX),
    termOfUse       NVARCHAR(MAX),
    carPrice        DECIMAL(18, 2),
    front           NVARCHAR(200),
    back            NVARCHAR(200),
    [left]          NVARCHAR(200),
    [right]         NVARCHAR(200),
    registration    NVARCHAR(200),
    certificate     NVARCHAR(200),
    insurance       NVARCHAR(200),
    brandId         INT,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (brandId) REFERENCES [dbo].[Brand] (brandId),
    );

-- Role table
CREATE TABLE [dbo].[Role]
(
    RoleId   INT IDENTITY (1,1) PRIMARY KEY,
    roleName NVARCHAR(50)
    );

insert into [Role]
values ('Admin'),
    ('Car Owner'),
    ('Customer'),
    ('Driver')

-- Booking Status table
CREATE TABLE [dbo].[BookingStatus]
(
    BookingStatusId INT IDENTITY (1,1) PRIMARY KEY,
    name            NVARCHAR(50)
    );

INSERT [dbo].[BookingStatus]  VALUES ( N'Pending deposit')
INSERT [dbo].[BookingStatus]  VALUES ( N'Confirmed')
INSERT [dbo].[BookingStatus]  VALUES ( N'In-Progress')
INSERT [dbo].[BookingStatus]  VALUES ( N'Pending payment')
INSERT [dbo].[BookingStatus]  VALUES ( N'Completed')
INSERT [dbo].[BookingStatus]  VALUES ( N'Cancelled')

-- Payment Method table
CREATE TABLE [dbo].[PaymentMethod]
(
    PaymentMethodId INT IDENTITY (1,1) PRIMARY KEY,
    name            NVARCHAR(50)
    );



INSERT [dbo].[PaymentMethod]  VALUES ( N'Wallet')
INSERT [dbo].[PaymentMethod]  VALUES ( N'VNPay')
INSERT [dbo].[PaymentMethod]  VALUES  (N'Money')
INSERT [dbo].[PaymentMethod]  VALUES ( N'MOMO')

-- Additional Function table
CREATE TABLE [dbo].[AdditionalFunction]
(
    AdditionalFunctionId INT IDENTITY (1,1) PRIMARY KEY,
    functionName         NVARCHAR(50)
    );


-- UserRole table (mapping between Users and Role)
CREATE TABLE [dbo].[UserRole]
(
    userId INT,
    roleId INT,
    PRIMARY KEY (userId, roleId),
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (roleId) REFERENCES [dbo].[Role] (RoleId)
    );

-- CarAdditionalFunction table (mapping between Car and Additional Functions)
CREATE TABLE [dbo].[CarAdditionalFunction]
(
    carId                INT,
    AdditionalFunctionId INT,
    PRIMARY KEY (carId, AdditionalFunctionId),
    FOREIGN KEY (carId) REFERENCES [dbo].[Car] (carId),
    FOREIGN KEY (AdditionalFunctionId) REFERENCES [dbo].[AdditionalFunction] (AdditionalFunctionId)
    );

-- Booking table
CREATE TABLE [dbo].[Booking]
(
    bookingId       INT IDENTITY (1,1) PRIMARY KEY,
    startDate       DATETIME,
    endDate         DATETIME,
    driverInfo      NVARCHAR(255),
    actualEndDate   DATETIME,
    totalPrice      DECIMAL(18, 2),
    userId          INT,
    bookingStatusId INT,
    paymentMethodId INT,
    driverId INT foreign key references [dbo].[Users] (userId) Null,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (bookingStatusId) REFERENCES [dbo].[BookingStatus] (BookingStatusId),
    FOREIGN KEY (paymentMethodId) REFERENCES [dbo].[PaymentMethod] (PaymentMethodId)
    );


-- Feedback table
CREATE TABLE [dbo].[Feedback]
(
    FeedbackId INT IDENTITY (1,1) PRIMARY KEY,
    bookingId  INT UNIQUE,
    rating     INT CHECK (rating BETWEEN 1 AND 5),
    content    NVARCHAR(MAX),
    dateTime   DATETIME
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking] (bookingId)
    );

-- Driver Details table
CREATE TABLE [dbo].[DriverDetail]
(
    driverId       INT IDENTITY (1,1) PRIMARY KEY,
    fullName       NVARCHAR(100),
    phone          NVARCHAR(15),
    nationalId     NVARCHAR(20),
    dob            DATE,
    email          NVARCHAR(100),
    drivingLicense NVARCHAR(50),
    city           NVARCHAR(100),
    district       NVARCHAR(100),
    ward           NVARCHAR(100),
    street         NVARCHAR(255),
    bookingId      INT,
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking] (bookingId)
    );

-- BookingCar table (mapping between Booking and Car)
CREATE TABLE [dbo].[BookingCar]
(
    carId     INT,
    bookingId INT,
    PRIMARY KEY (carId, bookingId),
    FOREIGN KEY (carId) REFERENCES [dbo].[Car] (carId),
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking] (bookingId)
    );

INSERT INTO Brand (brandName) VALUES ('Toyota'), ('Honda'), ('Hyundai'), ('Kia'), ('Mazda'), ('Ford'), ('Chevrolet'), ('Mercedes-Benz'), ('BMW'), ('Audi'), ('Lexus'), ('Nissan'), ('Volkswagen'), ('Peugeot'), ('Suzuki'), ('Subaru'), ('Mitsubishi'), ('Volvo'), ('Land Rover'), ('Jeep'), ('Porsche'), ('Jaguar'), ('Ferrari'), ('Lamborghini'), ('Rolls-Royce'), ('Bentley'), ('Bugatti'), ('Maserati'), ('McLaren'), ('Aston Martin'), ('Lotus'), ('Alfa Romeo'), ('Fiat'), ('Citroen'), ('Renault'), ('Skoda'), ('Seat'), ('Opel'), ('Dacia'), ('Lada'), ('ZAZ'), ('GAZ'), ('UAZ'), ('Moskvich'), ('Kamaz'), ('PAZ'), ('KAvZ'), ('Ikarus'), ('Neoplan'), ('Setra'), ('Van Hool'), ('Volvo'), ('Scania')

    INSERT INTO AdditionalFunction (functionName) VALUES ('GPS'), ('Child lock'), ('Sun roof'), ('DVD'), ('Ski Rack'), ('Car Cover'), ('Car Wash'), ('Car Wax'), ('Car Polish'), ('Car Vacuum'), ('Car Freshener'), ('Car Shampoo');

INSERT INTO CarStatus (name) VALUES ('AVAILABLE'), ('BOOKED'), ('STOPPED'), ('DELETED');

-- Transaction table
CREATE TABLE [dbo].[Transaction] (
                                     transactionId   INT IDENTITY (1,1) PRIMARY KEY,
    transactionType VARCHAR(20) NOT NULL CHECK (transactionType IN ('Withdraw', 'Top-up', 'Pay deposit', 'Receive deposit', 'Refund deposit', 'Offset final payment')),
    amount          DECIMAL(18, 2) NOT NULL,
    transactionDate DATETIME DEFAULT GETDATE(),
    userId          INT NOT NULL,
    bookingId       INT NULL, -- Used for booking-related transactions like deposits
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking] (bookingId)
    );






-- ĐÂY LÀ DỮ LIỆU TEST DRIVER, ANH EM TỤ THÊM TRONG BẢNG USER ROLE NHÉ , ROLE LÀ DRIVER(4)

INSERT INTO [dbo].[Users] (username, dob, email, nationalId, phone, drivingLicense, wallet, password, city, district, ward, street, fullName, agreeTerms, status, statusDriverId)
VALUES
    (N'john_doe', '1990-01-15', N'johndoe@example.com', N'123456789', N'0123456789', N'DL123456', 5000000.00, N'hashed_password_1', N'Ho Chi Minh', N'District 1', N'Ward 3', N'Pham Ngoc Thach', N'John Doe', 1, N'ACTIVATED', 1),
    (N'jane_smith', '1985-03-10', N'janesmith@example.com', N'987654321', N'0987654321', N'DL987654', 3000000.00, N'hashed_password_2', N'Hanoi', N'District 5', N'Ward 7', N'Le Duan', N'Jane Smith', 1, N'ACTIVATED', 1),
    (N'mike_brown', '1992-07-22', N'mikebrown@example.com', N'1122334455', N'0912345678', N'DL112233', 10000000.00, N'hashed_password_3', N'Da Nang', N'District 3', N'Ward 2', N'Nguyen Hue', N'Mike Brown', 1, N'ACTIVATED', 1);

INsert into UserRole
values(1, 4), (2,4),(3,4)

-- SQL to delete all information of carDraft, Car and relative information of Car
-- DELETE FROM CarDraft WHERE CarDraft.draftId > 0;
-- DELETE FROM CarAdditionalFunction WHERE CarAdditionalFunction.AdditionalFunctionId > 0;
-- DELETE FROM CarAddress WHERE CarAddress.addressId > 0;
-- DELETE FROM Car WHERE Car.carId > 0;


-- SQL to INSERT AN ADMIN
-- password: 123
DECLARE @InsertedUsers TABLE (userId BIGINT);

INSERT INTO [dbo].[Users] (username, dob, email, nationalId, phone, drivingLicense, wallet, password, city, district, ward, street, fullName, agreeTerms, status, statusDriverId)
OUTPUT inserted.userId INTO @InsertedUsers
VALUES (N'admin', '1990-01-15', N'admin@gmail.com', N'123456789', N'0123456789', N'DL123456', 9999999999.00,
        N'$2a$10$zTJMk41R7yUiRWED31NbtueNZTaIOV8mJm4HGavw2KIZVmCKgA8MW', N'Ha Noi', N'District 1',
        N'Ward 3', N'Cau Giay', N'Admin', 1, N'ACTIVATED', 1);

INSERT INTO [dbo].[UserRole] (userId, roleId)
SELECT userId, 1 -- 1 == Admin
FROM @InsertedUsers;



-- insert BookingStatus
GO
SET IDENTITY_INSERT [dbo].[BookingStatus] ON

INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (1, N'Pending deposit')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (2, N'Confirmed')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (3, N'In-Progress')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (4, N'Pending payment')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (5, N'Completed')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (6, N'Cancelled')
SET IDENTITY_INSERT [dbo].[BookingStatus] OFF

GO
SET IDENTITY_INSERT [dbo].[Booking] ON

INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (3, CAST(N'2024-11-12T10:00:00.000' AS DateTime), CAST(N'2024-11-14T10:00:00.000' AS DateTime), N'Driver A', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (4, CAST(N'2024-11-10T12:00:00.000' AS DateTime), CAST(N'2024-11-15T12:00:00.000' AS DateTime), N'Driver B', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 4, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (5, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver C', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (6, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver D', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (7, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver E', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 6, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (8, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Trong', CAST(N'2024-11-15T00:00:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (9, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver A', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 5, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (10, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver B', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (11, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver C', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (12, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver D', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 5, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (13, CAST(N'2024-11-05T14:00:00.000' AS DateTime), CAST(N'2024-11-14T14:00:00.000' AS DateTime), N'Driver E', CAST(N'2024-11-17T13:45:00.000' AS DateTime), CAST(1000000.00 AS Decimal(18, 2)), 4, 2, 3, NULL)
INSERT [dbo].[Booking] ([bookingId], [startDate], [endDate], [driverInfo], [actualEndDate], [totalPrice], [userId], [bookingStatusId], [paymentMethodId], [driverId]) VALUES (14, NULL, NULL, NULL, NULL, CAST(1000000.00 AS Decimal(18, 2)), NULL, NULL, NULL, NULL)
SET IDENTITY_INSERT [dbo].[Booking] OFF

GO
SET IDENTITY_INSERT [dbo].[Car] ON

INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (1, N'Toyota 3008 GT 2022', N'29G-11992', N'3008 GT', N'Red', 5, 2022, N'Automatic', N'Gasoline', CAST(21.00 AS Decimal(18, 2)), CAST(22.00 AS Decimal(18, 2)), CAST(1211111.00 AS Decimal(18, 2)), CAST(1111111.00 AS Decimal(18, 2)), N'Xe Đẹp Keng', N'No', CAST(231321321.00 AS Decimal(18, 2)), N'uploads\CarOwner\3\Car\1\frontImage.jpg', N'uploads\CarOwner\3\Car\1\backImage.jpg', N'uploads\CarOwner\3\Car\1\leftImage.jpg', N'uploads\CarOwner\3\Car\1\rightImage.jpg', N'uploads\CarOwner\3\Car\1\registration.png', N'uploads\CarOwner\3\Car\1\certificate.jpg', N'uploads\CarOwner\3\Car\1\insurance.jpg', CAST(N'2024-11-12T22:06:25.917' AS DateTime), 5, 1, 1)
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (2, N'Suzuki 3008 GT 2023', N'29G-11993', N'3008 GT', N'Red', 4, 2023, N'Manual', N'Gasoline', CAST(21.00 AS Decimal(18, 2)), CAST(22.00 AS Decimal(18, 2)), CAST(12222.00 AS Decimal(18, 2)), CAST(1111111.00 AS Decimal(18, 2)), N'Xe sieu dep', N'Đẹp thế nhỉ', CAST(123123123.00 AS Decimal(18, 2)), N'uploads\CarOwner\3\Car\2\frontImage.jpg', N'uploads\CarOwner\3\Car\2\backImage.jpg', N'uploads\CarOwner\3\Car\2\leftImage.jpg', N'uploads\CarOwner\3\Car\2\rightImage.jpg', N'uploads\CarOwner\3\Car\2\registration.jpg', N'uploads\CarOwner\3\Car\2\certificate.jpg', N'uploads\CarOwner\3\Car\2\insurance.jpg', CAST(N'2024-11-12T22:08:06.403' AS DateTime), 5, 15, 1)
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (3, N'BMW 730I 2022', N'30H-12344', N'730I', N'Grey', 4, 2022, N'Manual', N'Gasoline', CAST(11.00 AS Decimal(18, 2)), CAST(22.00 AS Decimal(18, 2)), CAST(1211111.00 AS Decimal(18, 2)), CAST(1111111.00 AS Decimal(18, 2)), N'Xe siêu xịn đi siêu sướng', N'Không còn gì để noái', CAST(11.00 AS Decimal(18, 2)), N'uploads\CarOwner\3\Car\3\frontImage.jpg', N'uploads\CarOwner\3\Car\3\backImage.jpg', N'uploads\CarOwner\3\Car\3\leftImage.jpg', N'uploads\CarOwner\3\Car\3\rightImage.jpg', N'uploads\CarOwner\3\Car\3\registration.jpg', N'uploads\CarOwner\3\Car\3\certificate.jpg', N'uploads\CarOwner\3\Car\3\insurance.jpg', CAST(N'2024-11-12T22:09:45.090' AS DateTime), 5, 9, 1)
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (4, N'Lexus 350 4MATIC 2022', N'18H-11111', N'350 4MATIC', N'Green', 2, 2022, N'Manual', N'Gasoline', CAST(11.00 AS Decimal(18, 2)), CAST(11.00 AS Decimal(18, 2)), CAST(1211111.00 AS Decimal(18, 2)), CAST(1111111.00 AS Decimal(18, 2)), N'111', N'11', CAST(11.00 AS Decimal(18, 2)), N'uploads\CarOwner\3\Car\4\frontImage.jpg', N'uploads\CarOwner\3\Car\4\backImage.jpg', N'uploads\CarOwner\3\Car\4\leftImage.jpg', N'uploads\CarOwner\3\Car\4\rightImage.jpg', N'uploads\CarOwner\3\Car\4\registration.jpg', N'uploads\CarOwner\3\Car\4\certificate.jpg', N'uploads\CarOwner\3\Car\4\insurance.jpg', CAST(N'2024-11-12T22:11:12.663' AS DateTime), 5, 11, 1)
INSERT [dbo].[Car] ([carId], [name], [licensePlate], [model], [color], [seatNo], [productionYear], [transmission], [fuel], [mileage], [fuelConsumption], [basePrice], [deposit], [description], [termOfUse], [carPrice], [front], [back], [left], [right], [registration], [certificate], [insurance], [lastModified], [userId], [brandId], [statusId]) VALUES (5, N'Hyundai Santafe 2024 2022', N'18F-39069', N'Santafe 2024', N'Green', 5, 2022, N'Automatic', N'Diesel', CAST(123123.00 AS Decimal(18, 2)), CAST(1232.00 AS Decimal(18, 2)), CAST(1230000.00 AS Decimal(18, 2)), CAST(900000.00 AS Decimal(18, 2)), N'AAAAAAAAAAAA', N'', CAST(444.00 AS Decimal(18, 2)), N'uploads\CarOwner\3\Car\5\frontImage.jpg', N'uploads\CarOwner\3\Car\5\backImage.jpg', N'uploads\CarOwner\3\Car\5\leftImage.jpg', N'uploads\CarOwner\3\Car\5\rightImage.jpg', N'uploads\CarOwner\3\Car\5\registration.jpg', N'uploads\CarOwner\3\Car\5\certificate.jpg', N'uploads\CarOwner\3\Car\5\insurance.jpg', CAST(N'2024-11-14T01:17:11.440' AS DateTime), 5, 3, 1)
SET IDENTITY_INSERT [dbo].[Car] OFF

