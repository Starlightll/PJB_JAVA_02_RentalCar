CREATE DATABASE RentalCar
GO
USE RentalCar
GO


-- Users table
CREATE TABLE [dbo].[Users]
(
    userId         INT IDENTITY (1,1) NOT NULL,
    username       NVARCHAR(50),
    avatar         NVARCHAR(MAX),
    dob            DATE,
    email          NVARCHAR(100)      NOT NULL,
    nationalId     NVARCHAR(20),
    phone          NVARCHAR(15)       NOT NULL,
    drivingLicense NVARCHAR(200),
    wallet         DECIMAL(18, 2),
    password       NVARCHAR(255)      NOT NULL,
    city           NVARCHAR(100),
    district       NVARCHAR(100),
    ward           NVARCHAR(100),
    street         NVARCHAR(255),
    fullName       NVARCHAR(100),
    agreeTerms     int                not null,
    status         VARCHAR(10)        NOT NULL CHECK (status IN ('PENDING', 'ACTIVATED', 'LOCKED', 'DELETED', 'RENTED')),
    salaryDriver   DECIMAL(18, 2),
    descriptionDriver    NVARCHAR(max),
    PRIMARY KEY (userId)
    );


-- Notification table
CREATE TABLE [dbo].[Notification]
(
    notificationId INT IDENTITY (1,1) PRIMARY KEY,
    content        NVARCHAR(MAX),
    isRead         BIT,
    userId         INT,
    createAt       DATETIME,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId)
    );

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
    CarStatusId INT PRIMARY KEY,
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
    carId           INT NULL,
    verifyStatus    NVARCHAR(20) NULL CHECK (verifyStatus IN ('Verified', 'Rejected', 'Pending', 'Cancelled')),
    FOREIGN KEY (carId) REFERENCES [dbo].Car(carId),
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
    BookingStatusId INT PRIMARY KEY,
    name            NVARCHAR(50)
    );



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
    lastModified    DATETIME DEFAULT CURRENT_TIMESTAMP,
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

INSERT INTO CarStatus (CarStatusId, name) VALUES (1,'Available'),
                                                 (2,'Booked'),
                                                 (3, 'Stopped'),
                                                 (4,'Deleted'),
                                                 (5, 'Maintenance'),
                                                 (6, 'Rented'),
                                                 (7,'Returned'),
                                                 (8,'Verifying'),
                                                 (9,'Confirmed'),
                                                 (10,'In-Progress'),
                                                 (11, 'Pending payment'),
                                                 (12, 'Completed'),
                                                 (13,'Cancelled'),
                                                 (14, 'Pending deposit'),
                                                 (15, 'Pending cancel'),
                                                 (16, 'Pending return')



-- insert BookingStatus
    INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (1, N'Pending deposit')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (2, N'Confirmed')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (3, N'In-Progress')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (4, N'Pending payment')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (5, N'Completed')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (6, N'Cancelled')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (7, N'Pending cancel')
INSERT [dbo].[BookingStatus] ([BookingStatusId], [name]) VALUES (8, N'Pending return')



-- Transaction table
CREATE TABLE [dbo].[Transaction] (
                                     transactionId   INT IDENTITY (1,1) PRIMARY KEY,
    transactionType VARCHAR(50) NOT NULL CHECK (transactionType IN
                                               ('Withdraw', 'Top-up', 'Pay deposit', 'Receive deposit',
                                                'Refund deposit', 'Offset final payment', 'Pay final payment',
                                                'Receive final payment','Receive remaining deposit',
                                                'Return remaining deposit', 'Pay for driver rental', 'Receive salary')),
    amount          DECIMAL(18, 2) NOT NULL,
    transactionDate DATETIME DEFAULT GETDATE(),
    userId          INT NOT NULL,
    bookingId       INT NULL, -- Used for booking-related transactions like deposits
    FOREIGN KEY (userId) REFERENCES [dbo].[Users] (userId),
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking] (bookingId)
    );

-- SQL to delete all information of carDraft, Car and relative information of Car
-- DELETE FROM CarDraft WHERE CarDraft.draftId > 0;
-- DELETE FROM CarAdditionalFunction WHERE CarAdditionalFunction.AdditionalFunctionId > 0;
-- DELETE FROM CarAddress WHERE CarAddress.addressId > 0;
-- DELETE FROM Car WHERE Car.carId > 0;


-- SQL to INSERT AN ADMIN
-- password: 123
DECLARE @InsertedUsers TABLE (userId BIGINT);

INSERT INTO [dbo].[Users] (username, dob, email, nationalId, phone, drivingLicense, wallet, password, city, district, ward, street, fullName, agreeTerms, status)
    OUTPUT inserted.userId INTO @InsertedUsers
VALUES (N'admin', '1990-01-15', N'admin@gmail.com', N'123456789', N'0123456789', N'DL123456', 9999999999.00,
    N'$2a$10$zTJMk41R7yUiRWED31NbtueNZTaIOV8mJm4HGavw2KIZVmCKgA8MW', N'1', N'8',
    N'334', N'Cau Giay', N'Admin', 1, N'ACTIVATED');

INSERT INTO [dbo].[UserRole] (userId, roleId)
SELECT userId, 1 -- 1 == Admin
FROM @InsertedUsers;


