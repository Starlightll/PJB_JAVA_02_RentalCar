let currentStep = document.getElementById('currentStep').value;
goToStep(currentStep ? parseInt(currentStep) : 1);
$(document).ready(function () {

    let current_fs, next_fs, previous_fs; //fieldsets
    let opacity;

    $(".next").click(async function () {
        console.log(pageStep);
        if (pageStep === 1) {
            console.log('Step1')
            if(!checkStep1()) {
                console.log("fail step 1");
                return;
            }
            // if (!await saveStep1()) {
            //     console.log("fail step 1 in AJAX");
            //     return;
            // }
            // Chờ kết quả từ saveBooking
            const success = await saveBooking();
            if (!success) {
                console.log("Booking save failed, stay at Step 1");
                return;
            }

            pageStep++;
        }else if (pageStep === 2) {

            // Chờ kết quả từ saveBooking
            const success = await saveBooking();
            if (!success) {
                console.log("Booking save failed, stay at Step 2");
                return; // Không chuyển bước nếu save thất bại
            }
            pageStep++;
        }else if (pageStep === 3) {
            pageStep++;
        }

        current_fs = $(this).parent();
        next_fs = $(this).parent().next();

        //Add Class Active
        $("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");

        //show the next fieldset
        next_fs.show();
        window.scrollTo(0, 0);
        //hide the current fieldset with style
        current_fs.animate({opacity: 0}, {
            step: function (now) {
                // for making fielset appear animation
                opacity = 1 - now;

                current_fs.css({
                    'display': 'none',
                    'position': 'relative'
                });
                next_fs.css({'opacity': opacity});
            },
            duration: 600
        });
    });

    $(".previous").click(function () {
        pageStep--;
        current_fs = $(this).parent();
        previous_fs = $(this).parent().prev();

        //Remove class active
        $("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");

        //show the previous fieldset
        previous_fs.show();
        // window.scrollTo(0, 0);

        //hide the current fieldset with style
        current_fs.animate({opacity: 0}, {
            step: function (now) {
                // for making fielset appear animation
                opacity = 1 - now;

                current_fs.css({
                    'display': 'none',
                    'position': 'relative'
                });
                previous_fs.css({'opacity': opacity});
            },
            duration: 600
        });
    });

    // $('.radio-group .radio').click(function () {
    //     $(this).parent().find('.radio').removeClass('selected');
    //     $(this).addClass('selected');
    // });

    $(".submit").click(function () {
        return false;
    })

});



function goToStep(targetStep) {
    if(targetStep > 4){
        targetStep = 4;
    }
    if(targetStep < 1){
        targetStep = 1;
    }
    pageStep = targetStep;

    // Cập nhật progress bar
    $("#progressbar li").removeClass("active");
    for (let i = 0; i < targetStep; i++) {
        $("#progressbar li").eq(i).addClass("active");
    }

    $("fieldset").hide();
    $("fieldset").eq(targetStep - 1).show();
}

// Call API to fetch provinces, districts and wards
// Function to create province/district/ward handlers for a specific form
// function createLocationHandlers(formId) {
//     console.log(formId);
//     const provinceSelect = document.getElementById('province' + formId);
//     const districtSelect = document.getElementById('district'+ formId);
//     const wardSelect = document.getElementById('ward' + formId);
//
//     // Fetch provinces
//     async function fetchProvinces(defaultCity) {
//         try {
//             const response = await fetch('https://provinces.open-api.vn/api/p/');
//             const provinces = await response.json();
//
//             provinceSelect.innerHTML = '<option value="">Select Province</option>';
//             provinces.forEach(province => {
//                 const option = document.createElement('option');
//                 option.value = province.code;
//                 option.textContent = province.name;
//                 if (province.code == defaultCity) {
//                     option.selected = true;
//                 }
//                 provinceSelect.appendChild(option);
//             });
//
//             if (defaultCity) {
//                 fetchDistricts(defaultCity, districtSelect.getAttribute('data-default'));
//             }
//         } catch (error) {
//             console.error('Error fetching provinces:', error);
//         }
//     }
//
//     // Fetch districts
//     async function fetchDistricts(provinceCode, defaultDistrict) {
//         try {
//             const response = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
//             const provinceData = await response.json();
//
//             districtSelect.innerHTML = '<option value="">Select District</option>';
//             provinceData.districts.forEach(district => {
//                 const option = document.createElement('option');
//                 option.value = district.code;
//                 option.textContent = district.name;
//                 if (district.code == defaultDistrict) {
//                     option.selected = true;
//                 }
//                 districtSelect.appendChild(option);
//             });
//
//             if (defaultDistrict) {
//                 fetchWards(defaultDistrict, wardSelect.getAttribute('data-default'));
//             }
//         } catch (error) {
//             console.error('Error fetching districts:', error);
//         }
//     }
//
//     // Fetch wards
//     async function fetchWards(districtCode, defaultWard) {
//         try {
//             const response = await fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`);
//             const districtData = await response.json();
//
//             wardSelect.innerHTML = '<option value="">Select Ward</option>';
//             districtData.wards.forEach(ward => {
//                 const option = document.createElement('option');
//                 option.value = ward.code;
//                 option.textContent = ward.name;
//                 if (ward.code == defaultWard) {
//                     option.selected = true;
//                 }
//                 wardSelect.appendChild(option);
//             });
//         } catch (error) {
//             console.error('Error fetching wards:', error);
//         }
//     }
//
//     // Add event listeners
//     provinceSelect.addEventListener('change', function() {
//         fetchDistricts(this.value);
//         wardSelect.innerHTML = '<option value="">Select Ward</option>';
//         districtSelect.disabled = this.value === "";
//         districtSelect.value = "";
//         wardSelect.disabled = true;
//     });
//
//     districtSelect.addEventListener('change', function() {
//         fetchWards(this.value);
//         wardSelect.disabled = this.value === "";
//     });
//
//     // Initial load
//     const defaultCity = provinceSelect.getAttribute('data-default');
//     fetchProvinces(defaultCity);
//
//     return {
//         fetchProvinces,
//         fetchDistricts,
//         fetchWards
//     };
// }
//
// // Hàm khởi tạo cho nhiều form
// document.addEventListener("DOMContentLoaded", function () {
//     const formIds = ['1', '2'];  // List các form ID bạn muốn xử lý
//     formIds.forEach(formId => {
//         console.log(formId);
//         console.log(formIds);
//
//         createLocationHandlers(formId);
//     });
// });
// Initialize both forms when the DOM is loaded
// document.addEventListener('DOMContentLoaded', function() {
//     // Initialize for Renter Information (form with ID '1')
//     const renterLocationHandlers = createLocationHandlers('1');
//
//     // Initialize for Driver Information (form with no ID suffix)
//     const driverLocationHandlers = createLocationHandlers('');
//
//     // Handle the checkbox for copying renter's information
//     const checkbox = document.querySelector('input[name="additionalFunction"]');
//     checkbox.addEventListener('change', function() {
//         if (!this.checked) {
//             // When unchecked, reset driver's form
//             document.getElementById('province').value = '';
//             document.getElementById('district').value = '';
//             document.getElementById('ward').value = '';
//             document.getElementById('district').disabled = true;
//             document.getElementById('ward').disabled = true;
//         } else {
//             // When checked, copy values from renter's form
//             const province1 = document.getElementById('province1');
//             const district1 = document.getElementById('district1');
//             const ward1 = document.getElementById('ward1');
//
//             document.getElementById('province').value = province1.value;
//             if (province1.value) {
//                 driverLocationHandlers.fetchDistricts(province1.value, district1.value);
//             }
//             if (district1.value) {
//                 driverLocationHandlers.fetchWards(district1.value, ward1.value);
//             }
//         }
//     });
// });


// Call API to fetch provinces, districts and wards
// Fetch provinces and set default city
async function fetchProvinces(defaultCity) {
    const response = await fetch('https://provinces.open-api.vn/api/p/');
    const provinces = await response.json();
    const citySelect = document.getElementById('province');

    provinces.forEach(province => {
        const option = document.createElement('option');
        option.value = province.code;
        option.textContent = province.name;
        if (province.code == defaultCity) {
            option.selected = true;
        }
        citySelect.appendChild(option);
    });

    if (defaultCity) {
        fetchDistricts(defaultCity, document.getElementById('district').getAttribute('data-default'));
    }
}

// Fetch districts based on selected province and set default value
async function fetchDistricts(provinceCode, defaultDistrict) {
    const response = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
    const provinceData = await response.json();
    const districtSelect = document.getElementById('district');

    districtSelect.innerHTML = '<option value="">Select District</option>';
    provinceData.districts.forEach(district => {
        const option = document.createElement('option');
        option.value = district.code;
        option.textContent = district.name;
        if (district.code == defaultDistrict) {
            option.selected = true;
        }
        districtSelect.appendChild(option);
    });

    if (defaultDistrict) {
        fetchWards(defaultDistrict, document.getElementById('ward').getAttribute('data-default'));
    }
}

// Fetch wards based on selected district and set default value
async function fetchWards(districtCode, defaultWard) {
    const response = await fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`);
    const districtData = await response.json();
    const wardSelect = document.getElementById('ward');

    wardSelect.innerHTML = '<option value="">Select Ward</option>';
    districtData.wards.forEach(ward => {
        const option = document.createElement('option');
        option.value = ward.code;
        option.textContent = ward.name;
        if (ward.code == defaultWard) {
            option.selected = true;
        }
        wardSelect.appendChild(option);
    });
}

// Select 2 library
$('#province').on('select2:select', function (e) {
    console.log("Province selected");
    const selectedProvince = $(this).val();
    fetchDistricts(selectedProvince);
    $('#ward').html('<option value="">Select Ward</option>').prop('disabled', true);
    $('#district').val("").prop('disabled', selectedProvince === "");
});


// Select 2 library
$('#district').on('select2:select', function (e) {
    console.log("District selected");
    const selectedDistrict = $(this).val();
    fetchWards(selectedDistrict);
    $('#ward').prop('disabled', selectedDistrict === "");
});

// Event listeners to load data on selection
// document.getElementById('province').addEventListener('change', function () {
//     fetchDistricts(this.value);
//     document.getElementById('ward').innerHTML = '<option value="">Select Ward</option>'; // Reset ward
//     document.getElementById('district').disabled = this.value === ""; // Enable/disable district based on city selection
//     document.getElementById('district').value = ""; // Reset district value
//     document.getElementById('ward').disabled = true; // Disable ward selection
// });
//
// document.getElementById('district').addEventListener('change', function () {
//     fetchWards(this.value);
//     document.getElementById('ward').disabled = this.value === ""; // Enable/disable ward based on district selection
// });


// Load provinces on page load with default values
document.addEventListener('DOMContentLoaded', function () {
    const defaultCity = document.getElementById('province').getAttribute('data-default');
    fetchProvinces(defaultCity);
});


//Load location
const province = document.getElementById('province');
const district = document.getElementById('district');
const ward = document.getElementById('district');
const previewLocation = document.getElementById('previewLocation');
ward.addEventListener('change', function(){
    previewLocation.textContent = province.options[province.selectedIndex].text + ', ' + district.options[district.selectedIndex].text + ', ' + ward.options[ward.selectedIndex].text;
});