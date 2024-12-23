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
    let pickupDateTime = document.querySelector('#pickupDateTime').value;
    if(pickupDateTime){
        pickupDateTime = new Date(pickupDateTime);
    }
    let now = new Date();
    if (now.getHours() < 5) {
        instance.set("minTime", "05:00");
        now.setHours(5, 0, 0, 0);
        instance.set("minDate", now);
        // instance.setDate(now);
    } else {
        now.setTime(now.getTime() + 60 * 60 * 1000);
        if (now.getHours() >= 23 || now.getHours() < 5) {
            let tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            tomorrow.setHours(5, 0, 0, 0);
            console.log("New time test:", tomorrow);
            instance.set("minTime", "05:00");
            instance.set("minDate", tomorrow);
            if(pickupDateTime <= tomorrow){
                instance.setDate(tomorrow);
            }
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
    let pickupDateTime = document.querySelector('#pickupDateTime').value;
    let now = new Date();
    if (pickupDateTime) {
        pickupDateTime = new Date(pickupDateTime);
        if(pickupDateTime > now){
            return pickupDateTime;
        }
    }
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
    if(oldDropDate > pickupDate){
        return oldDropDate;
    }
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
