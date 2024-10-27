
CREATE DATABASE RentalCar
USE RentalCar

-- Users table
CREATE TABLE [dbo].[Users]
(
    userId INT IDENTITY(1,1) NOT NULL,
    username NVARCHAR(50),
    dob DATE ,
    email NVARCHAR(100) NOT NULL,
    nationalId NVARCHAR(20) ,
    phone NVARCHAR(15) NOT NULL,
    drivingLicense NVARCHAR(50),
    wallet DECIMAL(18,2) ,
    password NVARCHAR(255) NOT NULL,
    city NVARCHAR(100) ,
    district NVARCHAR(100),
    ward NVARCHAR(100) ,
    street NVARCHAR(255),
    fullName NVARCHAR(100),
    agreeTerms int not null,
    status VARCHAR(10) NOT NULL CHECK (status IN('PENDING', 'ACTIVATED', 'LOCKED'))
    PRIMARY KEY (userId)
    );



CREATE TABLE VerificationToken
(
    tokenId INT IDENTITY(1,1) PRIMARY KEY,
    tokenCode NVARCHAR(MAX),
    expiryDate DATETIME,
    userId INT UNIQUE,                        -- Đảm bảo mỗi userId chỉ xuất hiện một lần
    CONSTRAINT FK_UserToken FOREIGN KEY (userId) REFERENCES users(userId)
);


-- Car table
CREATE TABLE [dbo].[Car]
(
    carId INT IDENTITY(1,1) PRIMARY KEY not null,
    name NVARCHAR(100),
    licensePlate NVARCHAR(20),
    model NVARCHAR(50),
    color NVARCHAR(20),
    seatNo INT,
    productionYear INT,
    transmission NVARCHAR(20),
    fuel NVARCHAR(20),
    mileage DECIMAL(18,2),
    fuelConsumption DECIMAL(18,2),
    basePrice DECIMAL(18,2),
    deposit DECIMAL(18,2),
    description NVARCHAR(MAX),
    termOfUse NVARCHAR(MAX),
    carPrice DECIMAL(18,2),
    userId INT,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users](userId)
    );

-- Role table
CREATE TABLE [dbo].[Role]
(
    RoleId INT IDENTITY(1,1) PRIMARY KEY,
    roleName NVARCHAR(50)
    );

insert into [Role]
values ( 'Admin'),
    ('Car Owner'),
    ( 'Customer' )



-- Feedback table
CREATE TABLE [dbo].[Feedback]
(
    FeedbackId INT IDENTITY(1,1) PRIMARY KEY,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    content NVARCHAR(MAX),
    dateTime DATETIME
    );

-- Images table
CREATE TABLE [dbo].[Images]
(
    ImagesId INT IDENTITY(1,1) PRIMARY KEY,
    front VARBINARY(MAX),
    back VARBINARY(MAX),
    [left] VARBINARY(MAX),
    [right] VARBINARY(MAX),
    carId INT,
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId)
    );

-- Booking Status table
CREATE TABLE [dbo].[BookingStatus]
(
    BookingStatusId INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50)
    );

-- Brand table
CREATE TABLE [dbo].[Brand]
(
    BrandId INT IDENTITY(1,1) PRIMARY KEY,
    brandName NVARCHAR(50),
    carId INT,
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId)
    );

-- Car Status table
CREATE TABLE [dbo].[CarStatus]
(
    CarStatusId INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50),
    carId INT,
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId)
    );


-- Payment Method table
CREATE TABLE [dbo].[PaymentMethod]
(
    PaymentMethodId INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50)
    );

-- Additional Function table
CREATE TABLE [dbo].[AdditionalFunction]
(
    AdditionalFunctionId INT IDENTITY(1,1) PRIMARY KEY,
    functionName NVARCHAR(50)
    );

-- Documents table
CREATE TABLE [dbo].[Documents]
(
    DocumentsId INT IDENTITY(1,1) PRIMARY KEY,
    registration NVARCHAR(100),
    certificate NVARCHAR(100),
    insurance NVARCHAR(100),
    carId INT,
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId)
    );

-- UserRole table (mapping between Users and Role)
CREATE TABLE [dbo].[UserRole]
(
    userId INT,
    roleId INT,
    PRIMARY KEY (userId, roleId),
    FOREIGN KEY (userId) REFERENCES [dbo].[Users](userId),
    FOREIGN KEY (roleId) REFERENCES [dbo].[Role](RoleId)
    );

-- CarAdditionalFunction table (mapping between Car and Additional Functions)
CREATE TABLE [dbo].[CarAdditionalFunction]
(
    carId INT,
    AdditionalFunctionId INT,
    PRIMARY KEY (carId, AdditionalFunctionId),
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId),
    FOREIGN KEY (AdditionalFunctionId) REFERENCES [dbo].[AdditionalFunction](AdditionalFunctionId)
    );

-- Booking table
CREATE TABLE [dbo].[Booking]
(
    bookingId INT IDENTITY(1,1) PRIMARY KEY,
    startDate DATETIME,
    endDate DATETIME,
    driverInfo NVARCHAR(255),
    actualEndDate DATETIME,
    totalPrice DECIMAL(18,2),
    userId INT,
    feedbackId INT,
    bookingStatusId INT,
    paymentMethodId INT,
    FOREIGN KEY (userId) REFERENCES [dbo].[Users](userId),
    FOREIGN KEY (feedbackId) REFERENCES [dbo].[Feedback](FeedbackId),
    FOREIGN KEY (bookingStatusId) REFERENCES [dbo].[BookingStatus](BookingStatusId),
    FOREIGN KEY (paymentMethodId) REFERENCES [dbo].[PaymentMethod](PaymentMethodId)
    );

-- Driver Details table
CREATE TABLE [dbo].[DriverDetail]
(
    driverId INT IDENTITY(1,1) PRIMARY KEY,
    fullName NVARCHAR(100),
    phone NVARCHAR(15),
    nationalId NVARCHAR(20),
    dob DATE,
    email NVARCHAR(100),
    drivingLicense NVARCHAR(50),
    city NVARCHAR(100),
    district NVARCHAR(100),
    ward NVARCHAR(100),
    street NVARCHAR(255),
    bookingId INT,
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking](bookingId)
    );

-- BookingCar table (mapping between Booking and Car)
CREATE TABLE [dbo].[BookingCar]
(
    carId INT,
    bookingId INT,
    PRIMARY KEY (carId, bookingId),
    FOREIGN KEY (carId) REFERENCES [dbo].[Car](carId),
    FOREIGN KEY (bookingId) REFERENCES [dbo].[Booking](bookingId)
    );
