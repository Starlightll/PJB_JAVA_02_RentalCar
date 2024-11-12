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
            pageStep++;
        }else if (pageStep === 2) {

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
function createLocationHandlers(formId) {
    console.log(formId);
    const provinceSelect = document.getElementById('province' + formId);
    const districtSelect = document.getElementById('district'+ formId);
    const wardSelect = document.getElementById('ward' + formId);

    // Fetch provinces
    async function fetchProvinces(defaultCity) {
        try {
            const response = await fetch('https://provinces.open-api.vn/api/p/');
            const provinces = await response.json();

            provinceSelect.innerHTML = '<option value="">Select Province</option>';
            provinces.forEach(province => {
                const option = document.createElement('option');
                option.value = province.code;
                option.textContent = province.name;
                if (province.code == defaultCity) {
                    option.selected = true;
                }
                provinceSelect.appendChild(option);
            });

            if (defaultCity) {
                fetchDistricts(defaultCity, districtSelect.getAttribute('data-default'));
            }
        } catch (error) {
            console.error('Error fetching provinces:', error);
        }
    }

    // Fetch districts
    async function fetchDistricts(provinceCode, defaultDistrict) {
        try {
            const response = await fetch(`https://provinces.open-api.vn/api/p/${provinceCode}?depth=2`);
            const provinceData = await response.json();

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
                fetchWards(defaultDistrict, wardSelect.getAttribute('data-default'));
            }
        } catch (error) {
            console.error('Error fetching districts:', error);
        }
    }

    // Fetch wards
    async function fetchWards(districtCode, defaultWard) {
        try {
            const response = await fetch(`https://provinces.open-api.vn/api/d/${districtCode}?depth=2`);
            const districtData = await response.json();

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
        } catch (error) {
            console.error('Error fetching wards:', error);
        }
    }

    // Add event listeners
    provinceSelect.addEventListener('change', function() {
        fetchDistricts(this.value);
        wardSelect.innerHTML = '<option value="">Select Ward</option>';
        districtSelect.disabled = this.value === "";
        districtSelect.value = "";
        wardSelect.disabled = true;
    });

    districtSelect.addEventListener('change', function() {
        fetchWards(this.value);
        wardSelect.disabled = this.value === "";
    });

    // Initial load
    const defaultCity = provinceSelect.getAttribute('data-default');
    fetchProvinces(defaultCity);

    return {
        fetchProvinces,
        fetchDistricts,
        fetchWards
    };
}

// Hàm khởi tạo cho nhiều form
document.addEventListener("DOMContentLoaded", function () {
    const formIds = ['1', '2'];  // List các form ID bạn muốn xử lý
    formIds.forEach(formId => {
        console.log(formId);
        console.log(formIds);

        createLocationHandlers(formId);
    });
});
