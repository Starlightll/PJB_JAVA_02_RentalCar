// Regex patterns cho biển số xe
const licensePlatePatterns = {
    // Dân sự (XXG-XXXXX)
    civilian: /^[0-9]{2}[A-Z]{1}-[0-9]{5}$/,

    // Format hiện đại (99A-999.99)
    modernCivilian: /^[0-9]{2}[A-Z][0-9]{3}\.[0-9]{2}$/,

    // Xe điện (99P9-999999)
    electric: /^[0-9]{2}P[0-9]-[0-9]{6}$/,

    // Pattern tổng hợp
    all: /^(([0-9]{2}[A-Z][0-9]-[0-9]{5})|([0-9]{2}[A-Z][0-9]{3}\.[0-9]{2})|([0-9]{2}P[0-9]-[0-9]{6}))$/
};




// Regex patterns for Money
const moneyPatterns = {
    // 0-9999999999
    civilian: /^\d+(\.\d{0,2})?$/
};

// Regex patterns for distance
const distancePatterns = {
    // 0-9999999999
    civilian: /^\d+(\.\d{0,2})?$/
}

// Regex patterns for material
const resourcePatterns = {
    // 0-9999999999
    liquid: /^\d+(\.\d{0,2})?$/
}



// Hàm setup validation cho License Plate
const setupLicensePlateValidation = (inputElement, msgElement) => {
    // Thêm class ẩn cho message ban đầu
    msgElement.style.display = 'none';

    const validateLicensePlate = (value) => {
        // Xóa khoảng trắng và chuyển sang chữ hoa
        const cleanValue = value.trim().toUpperCase();

        // Kiểm tra với pattern
        if (!licensePlatePatterns.civilian.test(cleanValue)) {
            return {
                isValid: false,
                message: 'Invalid license plate. Required format: XXG-XXXXX'
            };
        }

        return {
            isValid: true,
            value: cleanValue
        };
    };

    // Hàm update UI
    const updateUI = (result) => {
        if (result.isValid) {
            // Success state
            inputElement.classList.remove('is-invalid');
            inputElement.classList.add('is-valid');
            msgElement.style.display = 'none';
            msgElement.textContent = '';
        } else {
            // Error state
            inputElement.classList.remove('is-valid');
            inputElement.classList.add('is-invalid');
            msgElement.style.display = 'block';
            msgElement.style.color = '#dc3545';  // Bootstrap danger color
            msgElement.style.fontSize = '80%';
            msgElement.style.marginTop = '0.25rem';
            msgElement.textContent = result.message;
        }
    };

    // Validate on input
    inputElement.addEventListener('input', (e) => {
        const result = validateLicensePlate(e.target.value);
        updateUI(result);
    });

    // Validate on blur
    inputElement.addEventListener('blur', (e) => {
        const result = validateLicensePlate(e.target.value);
        updateUI(result);
    });

    // Return validate function for external use (e.g., form submit)
    return () => {
        const result = validateLicensePlate(inputElement.value);
        updateUI(result);
        return result.isValid;
    };
};

// Hàm setup validation cho Money
const setupMoneyValidation = (inputElement, msgElement) => {
    // Thêm class ẩn cho message ban đầu
    msgElement.style.display = 'none';

    const validateMoney = (value) => {
        // Xóa khoảng trắng
        const cleanValue = value.trim();

        // Kiểm tra với pattern
        if (!moneyPatterns.civilian.test(cleanValue) || cleanValue < 0 || cleanValue > 999999999) {
            return {
                isValid: false,
                message: 'Invalid money format. Required format: 0-999999999'
            };
        }

        return {
            isValid: true,
            value: cleanValue
        };
    };

    // Hàm update UI
    const updateUI = (result) => {
        if (result.isValid) {
            // Success state
            inputElement.classList.remove('is-invalid');
            inputElement.classList.add('is-valid');
            msgElement.style.display = 'none';
            msgElement.textContent = '';
        } else {
            // Error state
            inputElement.classList.remove('is-valid');
            inputElement.classList.add('is-invalid');
            msgElement.style.display = 'block';
            msgElement.style.color = '#dc3545';  // Bootstrap danger color
            msgElement.style.fontSize = '80%';
            msgElement.style.marginTop = '0.25rem';
            msgElement.textContent = result.message;
        }
    };

    // Validate on input
    inputElement.addEventListener('input', (e) => {
        const result = validateMoney(e.target.value);
        updateUI(result);
    });

    // Validate on blur
    inputElement.addEventListener('blur', (e) => {
        const result = validateMoney(e.target.value);
        updateUI(result);
    });

    // Return validate function for external use (e.g., form submit)
    return () => {
        const result = validateMoney(inputElement.value);
        updateUI(result);
        return result.isValid;
    };
};

// Hàm setup validation cho Distance
const setupRangeValidation = (inputElement, msgElement) => {
    // Thêm class ẩn cho message ban đầu
    msgElement.style.display = 'none';

    const validateRange = (value) => {
        // Xóa khoảng trắng xóa dấu chấm
        const cleanValue = value.toString().replace(/[.,\s]/g, '');

        // Kiểm tra với pattern
        if (!distancePatterns.civilian.test(cleanValue) || cleanValue < 0 || cleanValue > 1000000) {
            return {
                isValid: false,
                message: 'Invalid range format. Required format: 0-1000000'
            };
        }

        return {
            isValid: true,
            value: cleanValue
        };
    };

    // Hàm update UI
    const updateUI = (result) => {
        if (result.isValid) {
            // Success state
            inputElement.classList.remove('is-invalid');
            inputElement.classList.add('is-valid');
            msgElement.style.display = 'none';
            msgElement.textContent = '';
        } else {
            // Error state
            inputElement.classList.remove('is-valid');
            inputElement.classList.add('is-invalid');
            msgElement.style.display = 'block';
            msgElement.style.color = '#dc3545';  // Bootstrap danger color
            msgElement.style.fontSize = '80%';
            msgElement.style.marginTop = '0.25rem';
            msgElement.textContent = result.message;
        }
    };

    // Validate on input
    inputElement.addEventListener('input', (e) => {
        const result = validateRange(e.target.value);
        updateUI(result);
    });

    // Validate on blur
    inputElement.addEventListener('blur', (e) => {
        const result = validateRange(e.target.value);
        updateUI(result);
    });

    // Return validate function for external use (e.g., form submit)
    return () => {
        const result = validateRange(inputElement.value);
        updateUI(result);
        return result.isValid;
    };
};

// Hàm setup validation cho Material
const setupMaterialValidation = (inputElement, msgElement) => {
    // Thêm class ẩn cho message ban đầu
    msgElement.style.display = 'none';

    const validateMaterial = (value) => {
        // Xóa khoảng trắng xóa dấu chấm
        const cleanValue = value.toString().replace(/[.,\s]/g, '');

        // Kiểm tra với pattern
        if (!resourcePatterns.liquid.test(cleanValue) || cleanValue < 0 || cleanValue > 10000) {
            return {
                isValid: false,
                message: 'Invalid material format. Required format: 0-10000'
            };
        }

        return {
            isValid: true,
            value: cleanValue
        };
    };

    // Hàm update UI
    const updateUI = (result) => {
        if (result.isValid) {
            // Success state
            inputElement.classList.remove('is-invalid');
            inputElement.classList.add('is-valid');
            msgElement.style.display = 'none';
            msgElement.textContent = '';
        } else {
            // Error state
            inputElement.classList.remove('is-valid');
            inputElement.classList.add('is-invalid');
            msgElement.style.display = 'block';
            msgElement.style.color = '#dc3545';  // Bootstrap danger color
            msgElement.style.fontSize = '80%';
            msgElement.style.marginTop = '0.25rem';
            msgElement.textContent = result.message;
        }
    };

    // Validate on input
    inputElement.addEventListener('input', (e) => {
        const result = validateMaterial(e.target.value);
        updateUI(result);
    });

    // Validate on blur
    inputElement.addEventListener('blur', (e) => {
        const result = validateMaterial(e.target.value);
        updateUI(result);
    });

    // Return validate function for external use (e.g., form submit)
    return () => {
        const result = validateMaterial(inputElement.value);
        updateUI(result);
        return result.isValid;
    };
};


// //Validate for Model
// const setupModelValidation = (inputElement, msgElement) => {
//     // Thêm class ẩn cho message ban đầu
//     msgElement.style.display = 'none';
//
//     const validateModel = (value) => {
//         // Xóa khoảng trắng và chuyển sang chữ hoa
//         const cleanValue = value.trim();
//
//         // Kiểm tra với pattern
//         if (cleanValue === '') {
//             return {
//                 isValid: false,
//                 message: 'Model cannot be empty'
//             };
//         }
//
//         return {
//             isValid: true,
//             value: cleanValue
//         };
//     };
//
//     // Hàm update UI
//     const updateUI = (result) => {
//         if (result.isValid) {
//             // Success state
//             inputElement.classList.remove('is-invalid');
//             inputElement.classList.add('is-valid');
//             msgElement.style.display = 'none';
//             msgElement.textContent = '';
//         } else {
//             // Error state
//             inputElement.classList.remove('is-valid');
//             inputElement.classList.add('is-invalid');
//             msgElement.style.display = 'block';
//             msgElement.style.color = '#dc3545';  // Bootstrap danger color
//             msgElement.style.fontSize = '80%';
//             msgElement.style.marginTop = '0.25rem';
//             msgElement.textContent = result.message;
//         }
//     };
//
//     // Validate on input
//     inputElement.addEventListener('input', (e) => {
//         const result = validateModel(e.target.value);
//         updateUI(result);
//     });
//
//     // Validate on blur
//     inputElement.addEventListener('blur', (e) => {
//         const result = validateModel(e.target.value);
//         updateUI(result);
//     });
//
//     // Return validate function for external use (e.g., form submit)
//     return () => {
//         const result = validateModel(inputElement.value);
//         updateUI(result);
//         return result.isValid;
//     };
// };


// Initialize validation when document is ready

    const licensePlateInput = document.getElementById('licensePlate');
    const validateMsg = document.getElementById('LicensePlateValidate');
    const carPriceInput = document.getElementById('carPrice');
    const carPriceValidateMsg = document.getElementById('carPriceValidate');
    const basePriceInput = document.getElementById('basePrice');
    const basePriceValidateMsg = document.getElementById('basePriceValidate');
    const depositInput = document.getElementById('deposit');
    const depositValidateMsg = document.getElementById('depositValidate');
    const mileageInput = document.getElementById('mileage');
    const mileageValidateMsg = document.getElementById('mileageValidate');
    const fuelConsumptionInput = document.getElementById('fuelConsumption');
    const fuelConsumptionValidateMsg = document.getElementById('fuelConsumptionValidate');
    const modelInput = document.getElementById('model');
    const modelValidateMsg = document.getElementById('modelValidate');


    // Setup validation for license plate
    const validateLicensePlate = setupLicensePlateValidation(licensePlateInput, validateMsg);
    // Setup validation for money
    const validateCarPrice = setupMoneyValidation(carPriceInput, carPriceValidateMsg);
    const validateBasePrice = setupMoneyValidation(basePriceInput, basePriceValidateMsg);
    const validateDeposit = setupMoneyValidation(depositInput, depositValidateMsg);
    // Setup validation for distance
    const validateMileage = setupRangeValidation(mileageInput, mileageValidateMsg);
    // Setup validation for material
    const validateFuelConsumption = setupMaterialValidation(fuelConsumptionInput, fuelConsumptionValidateMsg);
    // Setup validation for model
    // const validateModel = setupModelValidation(modelInput, modelValidateMsg);


    // Optional: Validate on form submit
    const form = licensePlateInput.closest('form');


    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateLicensePlate()) {
                e.preventDefault(); // Prevent form submission if invalid
            }
        });
    }

    function validate(){
    if(!checkDetails() || !validateCarPrice() || !validateBasePrice() || !validateDeposit() || !validateFuelConsumption() || !validateMileage()) {
        console.log("Invalid data");
        return false
    }
    return true;
    }



