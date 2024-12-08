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
                                    if(input.value.trim() !== '') {
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
            if(file.size > MAX_FILE_SIZE) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'File size must be less than 2MB',
                    showConfirmButton: false,
                    timer: 1500
                });
                return;
            }
            if(!FILE_TYPES.includes(file.type)) {
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
