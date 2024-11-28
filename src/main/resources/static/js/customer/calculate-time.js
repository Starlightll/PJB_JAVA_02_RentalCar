//
//
// var car = /*[[${car}]]*/ {};
//
//
// //Khi load lại trang
// document.addEventListener('DOMContentLoaded', function () {
//
//     const pickupDate = document.getElementById('pickupDate').innerText;
//     const returnDate = document.getElementById('returnDate').innerText;
//     const newPickupDate = formatToDatetimeLocal(pickupDate);
//     const newReturnDate = formatToDatetimeLocal(returnDate);
//     console.log("pickupDate" ,newPickupDate)
//     console.log("returnDate" ,newReturnDate)
//
//     const { days, hours } = calculateNumberOfDays(newPickupDate, newReturnDate);
//
//     // Tính tiền theo ngày và giờ
//     const pricePerHour = car.basePrice / 24;
//     const totalPrice = (days * car.basePrice) + (hours * pricePerHour);
//     document.getElementById('totalPrice').innerText = totalPrice.toLocaleString('en-US', {
//         minimumFractionDigits: 0,
//         maximumFractionDigits: 0
//     }) + ' VND';
//     console.log("Tổng tiền: " + totalPrice);
//
// });
//
//
// function formatToDatetimeOnLoad(displayDate) {
//     // Tách ngày và giờ từ chuỗi input
//     const [date, time] = displayDate.split(" - ");
//     const [year, month, day] = date.split("/"); // Tách năm, tháng, ngày
//
//     // Định dạng lại thành chuỗi chuẩn ISO 8601: YYYY-MM-DDTHH:mm
//     return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}T${time}`;
// }
//
// function formatToDatetimeLocal(displayDate) {
//     const [date, time] = displayDate.split(" - ");
//     const [day, month, year] = date.split("/");
//     const [hour, minute] = time.split(":");
//     return `${year}-${month}-${day}T${hour}:${minute}`;
// }
//
// // Hàm tính số ngày và giờ
// function calculateNumberOfDays(startDate, endDate) {
//     const start = new Date(startDate);
//     const end = new Date(endDate);
//
//     // Lấy sự khác biệt thời gian (millisecond)
//     const diffTime = end - start;
//
//     if (diffTime <= 0) {
//         return { days: 0, hours: 0 }; // Nếu thời gian không hợp lệ
//     }
//
//     // Tính số ngày và giờ từ sự khác biệt
//     const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)); // Số ngày
//     const diffHours = Math.floor((diffTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)); // Số giờ lẻ
//
//     return { days: diffDays, hours: diffHours };
// }