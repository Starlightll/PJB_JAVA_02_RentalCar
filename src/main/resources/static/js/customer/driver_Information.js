document.addEventListener('DOMContentLoaded', () => {
    loadDriverList();
    loadUpdateDriverList()
});



function loadDriverList() {
    $.ajax({
        url: '/api/drivers', // Endpoint lấy danh sách tài xế
        type: 'GET',
        dataType: 'json',
        success: function (drivers) {
            const driverDropdown = $('#fullNameDriver');
            driverDropdown.empty(); // Xóa các option cũ
            driverDropdown.append('<option value="">Select a driver</option>'); // Thêm option mặc định

            // Duyệt qua danh sách drivers và thêm từng option
            $.each(drivers, function (index, driver) {
                driverDropdown.append(
                    `<option value="${driver.userId}">${driver.fullName + ' - ' + driver.phone}</option>`
                );
                console.log(driver.userId);
            });
        },
        error: function (xhr, status, error) {
            console.error('Error loading driver list:', error);
        },
    });
}





function fetchDriverDetailsAjax() {
    const selectedId = $('#fullNameDriver').val(); // Lấy ID tài xế từ option
    console.log("selectedId = ", selectedId);

    if (!selectedId) {
        // Làm sạch tất cả các trường
        $('#book_pick_dateDriver').val('');
        $('#phoneDriver').val('');
        $('#emailDriver').val('');
        $('#nationalIdDriver').val('');
        $('#streetDriver').val('');
        $('#previewImageDriver').attr('src', ''); // Xóa ảnh
        $('#provinceDriver').empty();
        $('#districtDriver').empty();
        $('#wardDriver').empty();
        $('#salaryDriver').empty();
        $('#descriptionDriver').empty();
        return;
    }


    $.ajax({
        url: `/api/drivers/${selectedId}`, // Endpoint lấy chi tiết tài xế
        type: 'GET',
        dataType: 'json',
        success: async function (driver) {
            // Điền thông tin tài xế vào các trường
            $('#book_pick_dateDriver').val(driver.dob);
            $('#phoneDriver').val(driver.phone);
            $('#emailDriver').val(driver.email);
            $('#nationalIdDriver').val(driver.nationalId);
            $('#streetDriver').val(driver.street);
            $('#salaryDriver').val(driver.salaryDriver);
            $('#descriptionDriver').val(driver.descriptionDriver);
            // Hiển thị ảnh bằng cách thay đổi src
             $('#previewImageDriver').attr('src', driver.avatar);
            // Gọi hàm để hiển thị tên địa điểm
            await displayLocationNames(driver.city, driver.district, driver.ward);
        },
        error: function (xhr, status, error) {
            console.error('Error fetching driver details:', error);
        },
    });
}

async function getLocationName(id, type) {
    let apiUrl = '';
    console.log("id = ",id)
    switch (type) {
        case 'province':
            apiUrl = `https://provinces.open-api.vn/api/p/${id}`;
            break;
        case 'district':
            apiUrl = `https://provinces.open-api.vn/api/d/${id}`;
            break;
        case 'ward':
            apiUrl = `https://provinces.open-api.vn/api/w/${id}`;
            break;
        default:
            return '';
    }

    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error(`API returned status ${response.status}`);
        }
        const data = await response.json();
        console.log(`${type} name fetched:`, data.name); // Log tên để kiểm tra
        return data.name;
    } catch (error) {
        console.error(`Error fetching ${type} name:`, error);
        return '';
    }
}



async function displayLocationNames(provinceId, districtId, wardId) {
    // Lấy tên Tỉnh
    const provinceName = await getLocationName(provinceId, 'province');
    if (provinceName) {
        $('#provinceDriver').append(`<option value="${provinceId}" selected>${provinceName}</option>`);
    } else {
        console.error('Province name not found for ID:', provinceId);
    }


    // Lấy tên huyện/quận
    const districtName = await getLocationName(districtId, 'district');
    $('#districtDriver').append(`<option value="${districtId}" selected>${districtName}</option>`);

    // Lấy tên xã/phường
    const wardName = await getLocationName(wardId, 'ward');
    $('#wardDriver').append(`<option value="${wardId}" selected>${wardName}</option>`);
}




//update booking

function loadUpdateDriverList() {

    const bookingId = $('#bookingId').val(); // Lấy ID tài xế từ option
    const driverId = $('#driverId').val(); // Lấy ID tài xế từ option

    console.log("Booking ID:", bookingId);
    console.log("Driver ID before AJAX call:", driverId);
    $.ajax({
        url: `/api/update-drivers/${bookingId}`,
        type: 'GET',
        dataType: 'json',
        success: function (drivers) {
            const driverDropdown = $('#fullNameDriver');
            driverDropdown.empty(); // Xóa các option cũ
            driverDropdown.append('<option value="">Select a driver</option>'); // Thêm option mặc định

            // Duyệt qua danh sách drivers và thêm từng option
            $.each(drivers, function (index, driver) {
                const option = $('<option>', {
                    value: driver.userId,
                    text: `${driver.fullName} - ${driver.phone}`,
                });

                // Gắn thuộc tính `selected` nếu ID khớp
                if (String(driver.userId) === String(driverId)) {
                    option.prop('selected', true);
                }

                driverDropdown.append(option);
            });

            // Làm mới Select2
            driverDropdown.trigger('change'); // Cập nhật Select2
            console.log("Dropdown options after update:", driverDropdown.html());
            console.log("Selected value:", driverDropdown.val());


            // Đặt giá trị để đảm bảo giao diện hiển thị đúng
            driverDropdown.val(driverId);


            // Nếu có driver được chọn, gọi hàm fetch chi tiết tài xế
            if (driverDropdown.val() === driverId) {
                fetchDriverDetailsAjax();
            }

        },
        error: function (xhr, status, error) {
            console.error('Error loading driver list:', error);
        },
    });
}