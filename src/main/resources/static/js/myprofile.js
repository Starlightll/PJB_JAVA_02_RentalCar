document.addEventListener('DOMContentLoaded', function () {
        // Track phone validation state
        let isPhoneBlurred = false;

        const e = document.querySelectorAll(".phone-number-mask"), t = document.getElementById("userFormUpdate");
        e && e.forEach(function (e) {
            new Cleave(e, {phone: !0, phoneRegionCode: "VN"})
        }),
            i = FormValidation.formValidation(t, {
                fields: {
                    dob: {
                        validators: {
                            date: {
                                format: "YYYY-MM-DD",
                                message: "The value is not a valid date"
                            },
                            callback: {
                                callback: function (input) {
                                    if (input.value !== "") {
                                        //Check the format of the date
                                        if (!/^\d{4}-\d{2}-\d{2}$/.test(input.value)) {
                                            return {
                                                valid: false,
                                                message: "The date of birth is not a valid date"
                                            }
                                        }
                                        const dob = input.value;
                                        const [year, month, day] = dob.split('-');
                                        const dobDate = new Date(year, month - 1, day);
                                        const today = new Date();
                                        const age = today.getFullYear() - dobDate.getFullYear();
                                        const monthDiff = today.getMonth() - dobDate.getMonth();
                                        const dayDiff = today.getDate() - dobDate.getDate();
                                        //Age must be greater than 18
                                        if (age < 18 || (age === 18 && (monthDiff < 0 || (monthDiff === 0 && dayDiff <= 0)))) {
                                            return {
                                                valid: false,
                                                message: "You must be at least 18 years old"
                                            }
                                        }
                                        //Age must be less than 100
                                        if (age > 100) {
                                            return {
                                                valid: false,
                                                message: "You must be less than 100 years old"
                                            }
                                        }
                                        return true;
                                    }
                                    return true;
                                }
                            },
                        }
                    },
                    phone: {
                        validators: {
                            // notEmpty: {
                            //     message: "Please enter your phone"
                            // },
                            callback: {
                                callback: function (input) {
                                    //Regex for phone vietnamese phone number
                                    //Remove all spaces, dashes, dots, and parentheses
                                    //The phone number must start with 03, 05, 07, 08, or 09
                                    //The phone number must have exactly 10 digits
                                    if (input.value.trim() === '') {
                                        return {
                                            valid: false,
                                            message: "The phone number is required"
                                        };
                                    } else {
                                        const phoneRegex = /^(0|\+84)(3|5|7|8|9)\d{8}$/;
                                        const formattedPhone = input.value.trim().replace(/[\s\\.\-\\(\\)]/g, '');
                                        const isValidFormat = phoneRegex.test(formattedPhone);
                                        if (!isValidFormat) {
                                            return {
                                                valid: false,
                                                message: "The phone number is not valid"
                                            };
                                        }
                                        if (isPhoneBlurred) {
                                            return validatePhone(input.value);
                                        }
                                        return true;
                                    }
                                }
                            },
                        }
                    },
                    nationalId: {
                        validators: {
                            stringLength: {
                                min: 9,
                                max: 12,
                                message: "The national ID must be 9 to 12 characters long"
                            },
                            callback: {
                                callback: function (input) {
                                    if (input.value.trim() !== '') {
                                        //Regex for only numbers
                                        const nationalIdRegex = /^\d+$/;
                                        const isValidFormat = nationalIdRegex.test(input.value);
                                        if (!isValidFormat) {
                                            return {
                                                valid: false,
                                                message: "The national must be a number"
                                            };
                                        }
                                        return validateNationalId(input.value);
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                },
                plugins: {
                    trigger: new FormValidation.plugins.Trigger,
                    bootstrap5: new FormValidation.plugins.Bootstrap5({
                        eleValidClass: "",
                        rowSelector: ".input"
                    }),
                    // submitButton: new FormValidation.plugins.SubmitButton,
                    // defaultSubmit: new FormValidation.plugins.DefaultSubmit,
                    autoFocus: new FormValidation.plugins.AutoFocus
                },

            });

        t.addEventListener("submit", function (e) {
            e.preventDefault();
            e.stopPropagation();
            i.validate().then(function (e) {
                if ("Valid" === e) {
                    const data = new FormData(t);
                    Swal.fire({
                        title: 'Are you sure?',
                        text: "You won't be able to revert this!",
                        icon: 'warning',
                        showCancelButton: !0,
                        confirmButtonText: 'Yes!',
                        cancelButtonText: 'No, cancel!',
                        reverseButtons: true,
                    }).then((result) => {
                        if (result.isConfirmed) {

                            // Check if files exist before adding to FormData
                            const fileInput = document.getElementById("inputDrivingLicense"); // ID của input file
                            if (fileInput && fileInput.files.length > 0) {
                                data.append("drivingLicenseFile", fileInput.files[0]);
                            }

                            fetch(`/update-profile`, {
                                method: 'POST',
                                headers: {
                                    // Include CSRF token if your app requires it
                                    'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
                                },
                                body: data,
                            }).then(response => {
                                if (response.ok) {
                                    response.json().then(data => {
                                        console.log("Submitted!!!");
                                        console.log(data);
                                        $('#editUserModal').modal('hide');
                                        throw Swal.fire({
                                            icon: 'success',
                                            title: 'Success',
                                            text: data.message,
                                            showConfirmButton: false,
                                            timer: 1500
                                        })
                                    });
                                } else {
                                    response.json().then(data => {
                                        console.log(data);
                                        Swal.fire({
                                            icon: 'warning',
                                            title: 'Warning',
                                            text: "Update profile failed",
                                            showConfirmButton: false,
                                            timer: 1500
                                        });
                                    });
                                }
                            });
                        }
                    });
                }
            })
        });

        //Reset form
        const resetBtn = document.getElementById('resetForm');
        resetBtn.addEventListener('click', function () {
            Swal.fire({
                title: 'Are you sure?',
                text: "You will lose all the changes!",
                icon: 'warning',
                showCancelButton: !0,
                confirmButtonText: 'Yes!',
                cancelButtonText: 'No, cancel!',
                reverseButtons: true,
            }).then((result) => {
                if (result.isConfirmed) {
                    reloadForm();
                    i.validate();
                }
            })
        });

        function reloadForm() {
            fetch(`/my-info`, {}).then(response => {
                if (response.ok) {
                    response.json().then(data => {
                        console.log(data);
                        console.log('User: ' + data);
                        t.querySelector('[name="fullName"]').value = data.fullName;
                        t.querySelector('[name="dob"]').value = data.dob;
                        t.querySelector('[name="nationalId"]').value = data.nationalId;
                        t.querySelector('[name="email"]').value = data.email;
                        t.querySelector('[name="phone"]').value = data.phone;
                        const defaultCity = data.city;
                        t.querySelector('[name="street"]').value = data.street;
                        fetchProvinces(defaultCity);
                        const drivingLicense = document.getElementById('drivingLicenseImage');
                        drivingLicense.innerHTML = `<img src="${data.drivingLicense}" alt="driving license" class="document-image">`;
                        checkDrivingLicense();
                    })
                }
            });
        }


        //Handle Upload Document
        const uploadBtn = document.getElementById('inputDrivingLicense');
        uploadBtn.addEventListener('change', function (e) {
            const file = e.target.files[0];
            const reader = new FileReader();
            reader.onload = function () {
                const img = document.getElementById('drivingLicenseImage');
                img.innerHTML = `<img src="${reader.result}" alt="driving license" class="document-image">`;
                checkDrivingLicense();
            }
            reader.readAsDataURL(file);
        });

        function checkDrivingLicense() {
            const fileInput = document.getElementById("inputDrivingLicense");
            if (fileInput && fileInput.files.length > 0) {
                try {
                    const drivingLicsenMessage = document.getElementById('drivingLicenseMessage');
                    drivingLicsenMessage.innerHTML = '';
                } catch (e) {

                }
            }
        }


        // Handle phone field blur/focus events
        const phoneField = t.querySelector('[name="phone"]');
        phoneField.addEventListener('blur', () => {
            isPhoneBlurred = true;
            i.revalidateField('phone');
        });

        phoneField.addEventListener('focus', () => {
            isPhoneBlurred = false;
            // i.revalidateField('phone');
        });

        const validatePhone = async function (phone) {
            const userId = t.querySelector('[name="id"]').value;
            const check = await checkPhoneAvailability(phone, userId);
            if (check) {
                console.log("Phone is available");
                return {
                    valid: true,
                    message: 'Phone is available',
                }
            } else {
                console.log("Phone is already taken");
                return {
                    valid: false,
                    message: 'Phone is already taken',
                }
            }
        }

        async function checkPhoneAvailability(phone, userId) {
            const response = await fetch(`/api/users/check-phone?phone=${phone}&userId=${userId}`);
            return await response.json();
        }

        const validateNationalId = async function (nationalId) {
            const userId = t.querySelector('[name="id"]').value;
            const check = await checkNationalIdAvailability(nationalId, userId);
            if (check) {
                console.log("National ID is available");
                return {
                    valid: true,
                    message: 'National ID is available',
                }
            } else {
                console.log("National ID is already taken");
                return {
                    valid: false,
                    message: 'National ID is already taken',
                }
            }
        }

        async function checkNationalIdAvailability(nationalId, userId) {
            const response = await fetch(`/api/users/check-nationalId?nationalId=${nationalId}&userId=${userId}`);
            return await response.json();
        }

        // Handle avatar change
        const MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
        const FILE_TYPES = ['image/png', 'image/jpg', 'image/jpeg'];
        const avatarInput = document.getElementById('avatarInput');
        avatarInput.addEventListener('change', function (e) {
            const file = e.target.files[0];
            if (file.size > MAX_FILE_SIZE) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'File size must be less than 2MB',
                    showConfirmButton: false,
                    timer: 1500
                });
                return;
            }
            if (!FILE_TYPES.includes(file.type)) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Invalid file type',
                    showConfirmButton: false,
                    timer: 1500
                });
                return;
            }
            Swal.fire({
                title: 'Are you sure?',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: !0,
                confirmButtonText: 'Yes!',
                cancelButtonText: 'No, cancel!',
                reverseButtons: true,
            }).then((result) => {
                if (result.isConfirmed) {
                    const data = new FormData();
                    data.append('avatar', file);
                    fetch(`/update-avatar`, {
                        method: 'POST',
                        headers: {
                            'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
                        },
                        body: data
                    }).then(response => {
                        if (response.ok) {
                            response.json().then(data => {
                                console.log(data);
                                const img = document.getElementById('avatarImage');
                                img.src = window.URL.createObjectURL(file);
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Success',
                                    text: data.message,
                                    showConfirmButton: false,
                                    timer: 1500
                                });
                            });
                        } else {
                            response.json().then(data => {
                                console.log(data);
                                Swal.fire({
                                    icon: 'warning',
                                    title: 'Warning',
                                    text: 'Update avatar failed',
                                    showConfirmButton: false,
                                    timer: 1500
                                });
                            });
                        }
                    });
                }
            });
        });
    }
)

document.addEventListener('DOMContentLoaded', function () {
    const changePasswordForm = document.getElementById("changePasswordForm");
    const changePasswordValidator = FormValidation.formValidation(changePasswordForm, {
        fields: {
            oldPassword: {
                validators: {
                    notEmpty: {
                        message: "The current password is required"
                    },
                    callback: {
                        callback: function () {
                            const oldPassword = changePasswordForm.querySelector('[name="oldPassword"]');
                            const oldPasswordHelp = document.getElementById('oldPasswordHelp');
                            oldPasswordHelp.innerHTML = '';
                            oldPassword.classList.remove('is-invalid');
                            return true;
                        },
                    }
                }
            },
            newPassword: {
                validators: {
                    callback: {
                        callback: function (input) {
                            const password = input.value;
                            if (password.trim() === '') {
                                const passwordStrengthLevel = document.getElementById('passwordStrengthLevel');
                                passwordStrengthLevel.style.width = '0%';
                                return {
                                    valid: false,
                                    message: "The password is required"
                                };
                            } else {
                                const hasNumber = /\d/.test(password);
                                const hasUpperCase = /[A-Z]/.test(password);
                                const hasLowerCase = /[a-z]/.test(password);
                                const hasSpecialChar = /[^a-zA-Z0-9]/.test(password);
                                let errors = [];

                                if (!hasUpperCase) {
                                    errors.push("Password must contain at least one capital letter");
                                } else {
                                    errors.push("<span style='color: green;'>Password must contain at least one capital letter</span>");
                                }
                                if (!hasLowerCase) {
                                    errors.push("Password must contain at least one lowercase letter");
                                } else {
                                    errors.push("<span style='color: green;'>Password must contain at least one lowercase letter</span>");
                                }
                                if (password.length < 8 || password.length > 32) {
                                    errors.push("Password must be at 8 - 30 characters long");
                                } else {
                                    errors.push("<span style='color: green;'>Password must be at 8 - 30 characters long</span>");
                                }
                                if (!hasNumber || !hasSpecialChar) {
                                    errors.push("Password must contain at least one number and one special character");
                                } else {
                                    errors.push("<span style='color: green;'>Password must contain at least one number and one special character</span>");
                                }


                                const passwordStrengthLevel = document.getElementById('passwordStrengthLevel');
                                let strength = (100 / 8) * calculatePasswordStrength(password);
                                console.log('Strength: ' + strength);
                                if (strength <= 0) {
                                    passwordStrengthLevel.style.width = '0%';
                                }
                                if (strength > 0 && strength <= 12.5) {
                                    passwordStrengthLevel.style.width = '12.5%';
                                    passwordStrengthLevel.style.backgroundColor = '#ff0000';
                                }
                                if (strength > 12.5 && strength <= 25) {
                                    passwordStrengthLevel.style.width = '25%';
                                    passwordStrengthLevel.style.backgroundColor = '#ff7f00';
                                }
                                if (strength > 25 && strength <= 37.5) {
                                    passwordStrengthLevel.style.width = '37.5%';
                                    passwordStrengthLevel.style.backgroundColor = '#ff9d00';
                                }
                                if (strength > 37.5 && strength <= 50) {
                                    passwordStrengthLevel.style.width = '50%';
                                    passwordStrengthLevel.style.backgroundColor = '#f7ff00';
                                }
                                if (strength > 50 && strength <= 62.5) {
                                    passwordStrengthLevel.style.width = '62.5%';
                                    passwordStrengthLevel.style.backgroundColor = '#aaff00';
                                }
                                if (strength > 62.5 && strength <= 74.5) {
                                    passwordStrengthLevel.style.width = '74.5%';
                                    passwordStrengthLevel.style.backgroundColor = '#aaff00';
                                }
                                if (strength > 74.5 && strength <= 87) {
                                    passwordStrengthLevel.style.width = '87%';
                                    passwordStrengthLevel.style.backgroundColor = '#00ff00';
                                }
                                if (strength > 87 && strength <= 100) {
                                    passwordStrengthLevel.style.width = '100%';
                                    passwordStrengthLevel.style.backgroundColor = '#00ff00';
                                }

                                if (!hasNumber || !hasUpperCase || !hasLowerCase || !hasSpecialChar || password.length < 8 || password.length > 32) {
                                    return {
                                        valid: false,
                                        message: errors.join('<br>')
                                    }
                                }
                                if (strength < 37.5) {
                                    return {
                                        valid: false,
                                        message: "Password is weak"
                                    }
                                }
                                const newPassword = changePasswordForm.querySelector('[name="newPassword"]').classList.add('is-valid');
                                return true;
                            }
                        },
                    }
                }
            },
            confirmPassword: {
                validators: {
                    callback: {
                        callback: function (input) {
                            if (input.value.trim() === '') {
                                return {
                                    valid: false,
                                    message: "The confirm password is required"
                                };
                            }
                            const newPassword = changePasswordForm.querySelector('[name="newPassword"]').value;
                            if (input.value !== newPassword) {
                                return {
                                    valid: false,
                                    message: "The confirm password does not match"
                                };
                            }
                            const confirmPassword = changePasswordForm.querySelector('[name="confirmPassword"]');
                            confirmPassword.classList.add('is-valid');
                            return true;
                        }
                    }
                }
            }
        },
        plugins: {
            trigger: new FormValidation.plugins.Trigger,
            bootstrap5: new FormValidation.plugins.Bootstrap5({
                eleValidClass: "",
                rowSelector: ".input"
            }),
            autoFocus: new FormValidation.plugins.AutoFocus
        }
    });

    changePasswordForm.addEventListener("submit", function (e) {
        e.preventDefault();
        e.stopPropagation();
        changePasswordValidator.validate().then(function (e) {
            if ("Valid" === e) {
                const data = new FormData(changePasswordForm);
                Swal.fire({
                    title: 'Are you sure?',
                    text: "You won't be able to revert this!",
                    icon: 'warning',
                    showCancelButton: !0,
                    confirmButtonText: 'Yes!',
                    cancelButtonText: 'No, cancel!',
                    reverseButtons: true,
                }).then((result) => {
                    if (result.isConfirmed) {
                        fetch(`/change-passwordv2`, {
                            method: 'POST',
                            headers: {
                                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
                            },
                            body: data
                        }).then(response => {
                            if (response.ok) {
                                response.json().then(data => {
                                    console.log(data);
                                    Swal.fire({
                                        icon: 'success',
                                        title: 'Success',
                                        text: data.message,
                                        showConfirmButton: false,
                                        timer: 1500
                                    }).then(() => {
                                        changePasswordForm.reset();
                                        const newPassword = document.getElementById('inputNewPassword');
                                        const confirmPassword = document.getElementById('inputConfirmPassword');
                                        const passwordStrengthLevel = document.getElementById('passwordStrengthLevel');
                                        passwordStrengthLevel.style.width = '0%';
                                        newPassword.classList.remove('is-valid');
                                        confirmPassword.classList.remove('is-valid');
                                    });
                                });
                            } else {
                                response.json().then(data => {
                                    console.log(data);
                                    if (data.wrongPass) {
                                        const oldPassword = changePasswordForm.querySelector('[name="oldPassword"]');
                                        const oldPasswordHelp = document.getElementById('oldPasswordHelp');
                                        oldPasswordHelp.innerHTML = '<span style="color: red;">' + data.wrongPass + '</span>';
                                        oldPassword.classList.add('is-invalid');
                                    } else {
                                        Swal.fire({
                                            icon: 'warning',
                                            title: 'Warning',
                                            text: 'Change password failed',
                                            showConfirmButton: false,
                                            timer: 1500
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    });

    function calculatePasswordStrength(password){
        let bonus = 0;
        const hasNumber = /\d/.test(password);
        const hasUpperCase = /[A-Z]/.test(password);
        const hasLowerCase = /[a-z]/.test(password);
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
        const hasNoRepeatedChar = !/(.)\1{2,}/.test(password);
        //Bonus length
        let lengthBonus = 0;
        const refinedPassword = removeExcessRepeats(password);
        if (refinedPassword.length >= 10) {
            lengthBonus = 0.5;
        }
        if (refinedPassword.length >= 12) {
            lengthBonus = 1;
        }
        if (refinedPassword.length >= 14) {
            lengthBonus = 1.5;
        }
        if (refinedPassword.length >= 16) {
            lengthBonus = 2;
        }
        //Bonus for having at least one of each
        let numberBonus = 0;
        if (hasNumber) {
            numberBonus++;
        }
        if (hasUpperCase) {
            numberBonus++;
        }
        if (hasLowerCase) {
            numberBonus++;
        }
        if (hasSpecialChar) {
            numberBonus++;
        }
        let totalBonusOfRegex = numberBonus * 0.5 + (4 - numberBonus) * -0.5;
        if (hasNoRepeatedChar && password.length >= 4) {
            bonus += 1;
        }
        if (isSequential(password) && password.length >= 4) {
            bonus += 1;
        }
        bonus += totalBonusOfRegex + lengthBonus;
        if(bonus < 0){
            bonus = 0;
        }
        return bonus;
    }

    function removeExcessRepeats(input) {
        return input.replace(/(.)\1{3,}/g, '$1$1$1');
    }

    function isSequential(input) {
        for (let i = 0; i < input.length - 2; i++) {
            const first = input.charCodeAt(i);
            const second = input.charCodeAt(i + 1);
            const third = input.charCodeAt(i + 2);

            if (second === first + 1 && third === second + 1) {
                return false;
            }

            if (second === first - 1 && third === second - 1) {
                return false;
            }
        }
        return true;
    }
});
