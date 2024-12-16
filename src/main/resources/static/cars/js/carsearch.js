document.addEventListener("DOMContentLoaded", function () {
    const toggleButtons = document.querySelectorAll(".toggle-button");

    toggleButtons.forEach(button => {
        button.addEventListener("click", function () {
            const targetId = button.getAttribute("data-target");
            const targetElement = document.querySelector(targetId);

            const isExpanded = button.getAttribute("aria-expanded") === "true";
            button.setAttribute("aria-expanded", !isExpanded);

            if (!isExpanded) {
                const height = targetElement.scrollHeight;
                targetElement.style.height = height + "px";
            } else {
                targetElement.style.height = "0";
            }
            button.classList.toggle("rotated");
        });
    });
});

// Initialize pickup datetime picker
const pickupDatePicker = flatpickr('#pickupDateTime', {
    allowInput: false,
    minDate: "today",
    enableTime: true,
    minTime: "05:00",
    maxTime: "23:00",
    minuteIncrement: 30,
    time_24hr: true,
    defaultDate: getDefaultDateForPickup(),
    onOpen: function (selectedDates, dateStr, instance) {
        adjustMinTimeForPickupDate(selectedDates, dateStr, instance);
        // Prevent typing in hour and minute fields
        const hourInput = instance.timeContainer.querySelector('.flatpickr-hour');
        const minuteInput = instance.timeContainer.querySelector('.flatpickr-minute');
        hourInput.setAttribute('readonly', 'readonly');
        minuteInput.setAttribute('readonly', 'readonly');
    },
    onChange: function (selectedDates, dateStr, instance) {
        adjustMinTimeForDropDate(true);
    }
});

// Initialize drop datetime picker
const dropDatePicker = flatpickr('#dropDateTime', {
    allowInput: false,
    minDate: "today",
    enableTime: true,
    minTime: "05:00",
    maxTime: "23:00",
    minuteIncrement: 30,
    time_24hr: true,
    defaultDate: getDefaultDateForDrop(),
    onOpen: function (selectedDates, dateStr, instance) {
        adjustMinTimeForDropDate();
        // Prevent typing in hour and minute fields
        const hourInput = instance.timeContainer.querySelector('.flatpickr-hour');
        const minuteInput = instance.timeContainer.querySelector('.flatpickr-minute');
        hourInput.setAttribute('readonly', 'readonly');
        minuteInput.setAttribute('readonly', 'readonly');
    },
    onChange: function (selectedDates, dateStr, instance) {
        adjustMinTimeForDropDate();
    }
});


function adjustMinTimeForPickupDate(selectedDates, dateStr, instance) {
    let now = new Date();
    if (now.getHours() < 5) {
        instance.set("minTime", "05:00");
        now.setHours(5, 0, 0, 0);
        instance.set("minDate", now);
        // instance.setDate(now);
    } else {
        now.setTime(now.getTime() + 60 * 60 * 1000);
        if (now.getHours() >= 23) {
            let tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(5, 0, 0, 0);
            console.log("New time test:", tomorrow);
            instance.set("minTime", "05:00");
            instance.set("minDate", tomorrow);
            instance.setDate(tomorrow);
        } else {
            let minTime = "";
            if (now.getMinutes() < 30) {
                now.setHours(now.getHours());
                now.setMinutes(0);
                minTime = `${now.getHours().toString().padStart(2, "0")}:${now.getMinutes().toString().padStart(2, "0")}`;
                console.log(minTime)
                instance.set("minTime", `${minTime}`);
                instance.set("minDate", now);
                // instance.setDate(now);
            }
            if (now.getMinutes() >= 30) {
                now.setHours(now.getHours());
                now.setMinutes(30);
                minTime = `${now.getHours().toString().padStart(2, "0")}:${now.getMinutes().toString().padStart(2, "0")}`;
                console.log(minTime)
                instance.set("minTime", `${minTime}`);
                instance.set("minDate", now);
                // instance.setDate(now);
            }
        }
    }
}

function getDefaultDateForPickup() {
    let now = new Date();
    let currentTime = now.getHours() + ":" + now.getMinutes();
    let currentHour = now.getHours();
    let currentMinute = now.getMinutes();
    now.setTime(now.getTime() + 60 * 60 * 1000);
    if (now.getHours() < 5) {
        now.setHours(5, 0, 0, 0);
        return now;
    } else {
        console.log("New time test:", now + " || " + now.getHours() + ":" + now.getMinutes());
        if (now.getHours() >= 23) {
            let tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(5, 0, 0, 0);
            console.log("New time test:", tomorrow);
            return tomorrow;
        } else {
            if (now.getMinutes() < 30) {
                now.setHours(now.getHours());
                now.setMinutes(0);
                return now;
            }
            if (now.getMinutes() >= 30) {
                now.setHours(now.getHours());
                now.setMinutes(30);
                return now;
            }
        }
    }
    console.log(currentTime);
}


function adjustMinTimeForDropDate(haveToSet = false) {
    const pickupDateTime = document.querySelector('#pickupDateTime').value;
    const dropDateTime = document.querySelector('#dropDateTime').value;
    const oldDropDate = new Date(dropDateTime);
    const pickupDate = new Date(pickupDateTime);
    pickupDate.setTime(pickupDate.getTime() + 2 * 60 * 60 * 1000);
    const dropDate = new Date(pickupDate);
    console.log("Time for drop:", dropDate);
    if (dropDate.getHours() < 5) {
        dropDate.setHours(5, 0, 0, 0);
        dropDatePicker.set("minTime", "05:00");
        dropDatePicker.set("minDate", dropDate);
        console.log(" h < 5");
        console.log(dropDate.getDate() + "-" + pickupDate.getDate() + "=" + (dropDate.getDate() - pickupDate.getDate()));
        if (oldDropDate.getDate() - pickupDate.getDate() < 1 && haveToSet === true && oldDropDate.getTime() - pickupDate.getTime() < 2 * 60 * 60 * 1000) {
            console.log("dropDate < pickupDate");
            dropDatePicker.setDate(dropDate);
        }
        // dropDatePicker.setDate(dropDate);
    } else {
        if (dropDate.getHours() >= 23) {
            let tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(5, 0, 0, 0);
            dropDatePicker.set("minTime", "05:00");
            dropDatePicker.set("minDate", tomorrow);
            console.log(" h >= 23");
            console.log(tomorrow.getDate() + "-" + pickupDate.getDate() + "=" + (tomorrow.getDate() - pickupDate.getDate()));
            if (oldDropDate.getDate() - pickupDate.getDate() < 1 && haveToSet === true && oldDropDate.getTime() - pickupDate.getTime() < 2 * 60 * 60 * 1000) {
                dropDatePicker.setDate(tomorrow);
            }
            // dropDatePicker.setDate(tomorrow);
        } else {
            let minTime = "";
            console.log(" h < 23 và h >= 5");
            if (dropDate.getMinutes() < 30) {
                dropDate.setHours(dropDate.getHours());
                dropDate.setMinutes(0);
                minTime = `${dropDate.getHours().toString().padStart(2, "0")}:${dropDate.getMinutes().toString().padStart(2, "0")}`;
                console.log(minTime)
                dropDatePicker.set("minTime", `${minTime}`);
                dropDatePicker.set("minDate", dropDate);
                console.log(dropDate.getDate() + "-" + pickupDate.getDate() + "=" + (dropDate.getDate() - pickupDate.getDate()));
                if (oldDropDate.getDate() - pickupDate.getDate() < 1 && haveToSet === true && oldDropDate.getTime() - pickupDate.getTime() < 2 * 60 * 60 * 1000) {
                    dropDatePicker.setDate(dropDate);
                }
                // dropDatePicker.setDate(dropDate);
            }
            if (dropDate.getMinutes() >= 30) {
                dropDate.setHours(dropDate.getHours());
                dropDate.setMinutes(30);
                minTime = `${dropDate.getHours().toString().padStart(2, "0")}:${dropDate.getMinutes().toString().padStart(2, "0")}`;
                console.log(minTime)
                dropDatePicker.set("minTime", `${minTime}`);
                dropDatePicker.set("minDate", dropDate);
                console.log(dropDate.getDate() + "-" + pickupDate.getDate() + "=" + (dropDate.getDate() - pickupDate.getDate()));
                if (oldDropDate.getDate() - pickupDate.getDate() < 1 && haveToSet === true && oldDropDate.getTime() - pickupDate.getTime() < 2 * 60 * 60 * 1000) {
                    dropDatePicker.setDate(dropDate);
                }
                // dropDatePicker.setDate(dropDate);
            }
        }
    }
    if (oldDropDate.getDate() - pickupDate.getDate() >= 1) {
        dropDatePicker.set("minTime", "05:00");
    }
}


function getDefaultDateTime(timePlus) {
    const now = new Date();
    const currentHour = now.getHours();
    if (!timePlus) {
        timePlus = 0;
    }

    if (currentHour < 5) {
        // If it's before 5 AM, set to 5 AM today
        const defaultDate = new Date(now);
        defaultDate.setHours(5 + timePlus, 0, 0, 0);
        return defaultDate;
    } else {
        // Set to current time + 1 hour
        const defaultDate = new Date(now);
        defaultDate.setHours(currentHour + 1 + timePlus, 0, 0, 0);

        // If the calculated time would be after 21:00, set to 21:00
        if (defaultDate.getHours() > 21) {
            defaultDate.setDate(defaultDate.getDate() + 1);
            defaultDate.setHours(5, 0, 0, 0);
        }
        return defaultDate;
    }
}


function getDefaultDateForDrop() {
    const pickupDateTime = document.querySelector('#pickupDateTime').value;
    const dropDateTime = document.querySelector('#dropDateTime').value;
    const oldDropDate = new Date(dropDateTime);
    const pickupDate = new Date(pickupDateTime);
    pickupDate.setTime(pickupDate.getTime() + 2 * 60 * 60 * 1000);
    const dropDate = new Date(pickupDate);
    console.log("Time for drop:", dropDate);
    if (dropDate.getHours() < 5) {
        dropDate.setHours(5, 0, 0, 0);
        return dropDate;
    } else {
        if (dropDate.getHours() >= 23) {
            let tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(5, 0, 0, 0);
            return tomorrow;
        } else {
            console.log(" h < 23 và h >= 5");
            if (dropDate.getMinutes() < 30) {
                dropDate.setHours(dropDate.getHours());
                dropDate.setMinutes(0);
                return dropDate;
            }
            if (dropDate.getMinutes() >= 30) {
                dropDate.setHours(dropDate.getHours());
                dropDate.setMinutes(30);
                return dropDate;
            }
        }
    }
}

const dropDateTime = document.querySelector('#dropDateTime');
dropDateTime.addEventListener('change', adjustMinRentTime);

function adjustMinRentTime() {
    const pickupInfoHelp = document.getElementById('pickupInfoHelp');
    const pickupDateTime = document.querySelector('#pickupDateTime').value;
    const dropDateTime = document.querySelector('#dropDateTime').value;

    if (!pickupDateTime || !dropDateTime) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Please select both pickup and drop times</span>';
        return false;
    }

    const pickupDate = new Date(pickupDateTime);
    const dropDate = new Date(dropDateTime);

    if (isNaN(pickupDate.getTime()) || isNaN(dropDate.getTime())) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Invalid date or time selected</span>';
        return false;
    }

    const diffHours = (dropDate - pickupDate) / 1000 / 60 / 60;

    if (diffHours < 2) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Minimum rent time is 2 hours</span>';
        return false;
    }

    pickupInfoHelp.textContent = '';
}

function validateTimeInput() {
    const pickupInfoHelp = document.getElementById('pickupInfoHelp');
    const pickupDateTime = document.getElementById('pickupDateTime').value;
    const dropDateTime = document.getElementById('dropDateTime').value;
    const pickupDate = new Date(pickupDateTime);
    const dropDate = new Date(dropDateTime);
    if (pickupDateTime === '' || dropDateTime === '') {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Please select both pickup and drop times</span>';
        return false;
    }
    if (isNaN(pickupDate.getTime()) || isNaN(dropDate.getTime())) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Invalid date or time selected</span>';
        return false;
    }
    if (pickupDate < new Date()) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Pickup time must be in the future</span>';
        return false;
    }
    if (dropDate < new Date()) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Drop time must be in the future</span>';
        return false;
    }
    if (pickupDate > dropDate) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Drop time must be after pickup time</span>';
        return false;
    }
    //Check if pickupDate or dropDate is not in range of 5:00 - 21:00
    if (pickupDate.getHours() < 5 || pickupDate.getHours() > 21) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Pickup time must be in range of 5:00 - 21:00</span>';
        return false;
    }
    if (dropDate.getHours() < 5 || dropDate.getHours() > 21) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Drop time must be in range of 5:00 - 21:00</span>';
        return false;
    }
    const diffHours = (dropDate - pickupDate) / 1000 / 60 / 60;
    if (diffHours < 2) {
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Minimum rent time is 2 hours</span>';
        return false;
    }


    return adjustMinRentTime() !== false;

}


const _csrf = document.querySelector('meta[name="_csrf"]').content;


function fetchCars(pickupLocation, pickupDateTime, dropDateTime) {
    const loader = document.getElementById("ftco-loader-car");
    const carContainer = document.getElementById("carContainer");
    const selectedBrands = Array.from(document.querySelectorAll('input[name="brands"]:checked')).map(checkbox => checkbox.value);
    const selectedFunctions = Array.from(document.querySelectorAll('input[name="functions"]:checked')).map(checkbox => checkbox.value);

    const queryParams = new URLSearchParams({
        pickupLocation: pickupLocation,
        pickupDateTime: pickupDateTime,
        dropDateTime: dropDateTime,
        selectedBrands: selectedBrands.join(','),
        selectedFunctions: selectedFunctions.join(',')
    }).toString();

    loader.style.opacity = '1';
    carContainer.innerHTML = "";

    fetch(`/api/searchCar?${queryParams}`, {
        method: 'GET',
        headers: {
            // 'Content-Type': 'application/json',
            'X-CSRF-TOKEN': _csrf
        },
        // body: JSON.stringify({
        //     selectedBrands: selectedBrands,
        //     selectedFunctions: selectedFunctions
        // })
    })
        .then(response => response.json())
        .then(data => {
            loader.style.opacity = '0';
            if (data.length === 0) {
                carContainer.innerHTML = '<div style="margin: auto"><h3 data-cursor="-opaque" style="color: grey">There\'re no cars that are available for you!</h3></div>';
                return;
            }
            //Store data to local storage
            localStorage.setItem('cars', JSON.stringify(data));
            let wowDelay = 0.1;
            data.forEach(car => {
                // Determine status style based on statusId
                let statusStyle = '';
                switch (car.carStatus.statusId) {
                    case 1:
                        statusStyle = 'color: #2c8b00; background-color: #e2ffde';
                        break;
                    case 2:
                        statusStyle = 'color: #7300ff; background-color: #f3edff';
                        break;
                    case 3:
                        statusStyle = 'color: #ff0000; background-color: #ffdede';
                        break;
                    case 5:
                        statusStyle = 'color: #3d3d3d; background-color: #e4e4e4';
                        break;
                    case 6:
                        statusStyle = 'color: #ff8c00; background-color: #ffedde';
                        break;
                    case 10:
                        statusStyle = 'color: #00c4ff; background-color: #defbff';
                        break;
                }


                carContainer.innerHTML += `
                        <div class="col-xl-4 col-lg-6 col-md-6 wow zoomIn" data-wow-delay="${wowDelay += 0.1}s">
                            <div class="perfect-fleet-item fleets-collection-item" style="display: flex; flex-direction: column">
                                <div class="image-box" style="border-radius: 15px; overflow: hidden; height: 200px; align-content: center;">
                                    <img src="${car.frontImage}"
                                     style="width: 100%; max-width: none !important; border-radius: 14px; object-fit: cover; height: 100%;"
                                     alt="">
                                </div>

                            <div class="perfect-fleet-content">
                                <div class="perfect-fleet-title">
                                    <div style="display: flex;justify-content: space-between;flex-flow: wrap;"><h3 style="${statusStyle}">${car.carStatus.name}</h3><span style="color: #ffb825"><i class="fa fa-star" style="margin-right: 3px"></i>${car.rateAverage}</span></div>
                                    <h2>${car.carName}</h2>
                                    <div class="mt-3 d-flex ">
                                     <i class="fa fa-location-arrow mr-1" style="margin-top: 5px;"></i>
                                     <span style="font-size: 0.9rem">${car.address.province}, ${car.address.district}</span>
                                    </div>
                                </div>

                                <div class="perfect-fleet-body">
                                    <ul>
                                        <li>
                                            <i class="fa fa-user"></i>
                                            <p class="m-0">${car.seat} passenger</p>
                                        </li>
                                        <li>
                                            <i class="fa fa-sliders"></i>
                                            <p class="m-0">${car.transmission}</p>
                                        </li>
                                        <li>
                                            <i class="fa fa-gas-pump"></i>
                                            <p class="m-0">${car.fuelType}</p>
                                        </li>
                                    </ul>
                                </div>
                            </div>

                            <div class="perfect-fleet-footer">
                                <div class="perfect-fleet-pricing d-flex">
                                    <h2 data-cursor="-opaque"
                                        class="basePrice"
                                        title="${new Intl.NumberFormat().format(car.basePrice)}"
                                        data-bs-toggle="tooltip"
                                        data-bs-placement="top">
                                        ${car.basePrice}
                                    </h2>
                                    <span style="font-size: 14px;font-weight: 400; align-self: center">/day</span>
                                </div>

                                    <div class="perfect-fleet-btn">
                                        <a href="#" class="section-icon-btn" onclick="viewCarDetail(${car.carId})">
                                            <img src="images/arrow-white.svg" alt="">
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>`;
            });
            formatMoney();
        })
        .catch(error => {
            console.error("Error fetching cars:", error);
            loader.style.opacity = '0';
            carContainer.innerHTML = "<h1>Error loading cars!</h1>";
        });
}

function displayCars() {

}


function fetchBrands() {
    const brandList = document.getElementById('brandList');

    fetch(`/api/brands`)
        .then(response => response.json())
        .then(data => {
            const fragment = document.createDocumentFragment();

            if (data.length === 0) {
                const noData = document.createElement('ul');
                noData.innerHTML = '<li><p>No brands found</p></li>';
                fragment.appendChild(noData);
            } else {
                data.forEach(brand => {
                    if(brand.cars.length === 0) return;
                    const label = document.createElement('label');
                    label.className = 'checkbox bounce col-12 p-0 d-flex';
                    label.style.cssText = 'align-items: center; column-gap: 6px;';
                    label.innerHTML = `<input type="checkbox" value="${brand.brandId}" name="brands">
                                               <svg viewBox="0 0 21 21">
                                                  <polyline points="5 10.75 8.5 14.25 16 6"></polyline>
                                               </svg>
                                               <span>${brand.brandName} (${brand.cars.length})</span>`;
                    fragment.appendChild(label);
                });
            }

            brandList.innerHTML = '';
            brandList.appendChild(fragment);
        })
        .catch(error => {
            console.error("Error fetching brands:", error);
            brandList.innerHTML = '<p>Error loading brands</p>';
        });
}

function fetchAdditionalFunction(){
    const functionList = document.getElementById('functionList');

    fetch(`/api/additionalFunctions`)
        .then(response => response.json())
        .then(data => {
            const fragment = document.createDocumentFragment();

            if (data.length === 0) {
                const noData = document.createElement('ul');
                noData.innerHTML = '<li><p>No additional functions found</p></li>';
                fragment.appendChild(noData);
            } else {
                data.forEach(additionalFunction => {
                    const label = document.createElement('label');
                    label.className = 'checkbox bounce col-12 p-0 d-flex';
                    label.style.cssText = 'align-items: center; column-gap: 6px;';
                    label.innerHTML = `<input type="checkbox" value="${additionalFunction.functionId}" name="functions">
                                               <svg viewBox="0 0 21 21">
                                                  <polyline points="5 10.75 8.5 14.25 16 6"></polyline>
                                               </svg>
                                               <span>${additionalFunction.functionName}</span>`;
                    fragment.appendChild(label);
                });
            }

            functionList.innerHTML = '';
            functionList.appendChild(fragment);
        })
        .catch(error => {
            console.error("Error fetching additional functions:", error);
            functionList.innerHTML = '<p>Error loading additional functions</p>';
        });
}


function formatMoney() {
    const priceElements = document.querySelectorAll('.basePrice');

    priceElements.forEach(priceElement => {
        const price = parseFloat(priceElement.textContent);
        priceElement.textContent = new Intl.NumberFormat('vi-VN', {
            style: 'currency',
            currency: 'VND'
        }).format(price);
    });
}

document.getElementById('fleetsForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const pickupLocation = document.getElementById('pickupLocation').value;
    const pickupDateTime = document.getElementById('pickupDateTime').value;
    const dropDateTime = document.getElementById('dropDateTime').value;

    if (pickupDateTime === '' || dropDateTime === '') {
        const pickupInfoHelp = document.getElementById('pickupInfoHelp');
        pickupInfoHelp.innerHTML = '<i class="fa fa-warning"></i><span> Please select both pickup and drop times</span>';
        return;
    }
    if (validateTimeInput() === false) {
        return;
    }
    console.log(pickupLocation, pickupDateTime, dropDateTime);
    fetchCars(pickupLocation, pickupDateTime, dropDateTime);
});

function viewCarDetail(carId) {
    const pickupLocation = document.getElementById('pickupLocation').value;
    const pickupDateTime = document.getElementById('pickupDateTime').value;
    const dropDateTime = document.getElementById('dropDateTime').value;
    window.location.href = `/api/searchCar/${carId}?pickupLocation=${pickupLocation}&pickupDateTime=${pickupDateTime}&dropDateTime=${dropDateTime}`;
}
