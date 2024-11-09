let currentStep = document.getElementById('currentStep').value;
goToStep(currentStep ? parseInt(currentStep) : 1);
$(document).ready(function () {

    let current_fs, next_fs, previous_fs; //fieldsets
    let opacity;

    $(".next").click(async function () {
        console.log(pageStep);
        if (pageStep === 1) {
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