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
                    `<option value="${driver.userId}">${driver.fullName}</option>`
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
            // Hiển thị ảnh bằng cách thay đổi src
            $('#previewImageDriver').attr('src', driver.drivingLicense);
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
    console.log("selectedId = ", bookingId);
    const driverId = $('#driverId').val(); // Lấy ID tài xế từ option
    console.log("selectedId = ", driverId);

    $.ajax({
        url: `/api/update-drivers/${bookingId}`, // Endpoint lấy danh sách tài xế
        type: 'GET',
        dataType: 'json',
        success: function (drivers) {
            const driverDropdown = $('#fullNameDriver');
            driverDropdown.empty(); // Xóa các option cũ
            driverDropdown.append('<option value="">Select a driver</option>'); // Thêm option mặc định
            let hasSelectedDriver = false;
            // Duyệt qua danh sách drivers và thêm từng option
            $.each(drivers, function (index, driver) {
                const isSelected = String(driver.userId) === String(driverId) ? 'selected' : '';
                if (isSelected) {
                    hasSelectedDriver = true;
                }
                driverDropdown.append(
                    `<option value="${driver.userId}" ${isSelected}>${driver.fullName}</option>`
                );
            });

            if (hasSelectedDriver) {
                fetchDriverDetailsAjax();
            }
            console.log(driver.userId);
            console.log("isSelected = ", isSelected);
        },
        error: function (xhr, status, error) {
            console.error('Error loading driver list:', error);
        },
    });
}