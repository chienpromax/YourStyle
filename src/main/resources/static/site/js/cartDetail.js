document.addEventListener("DOMContentLoaded", function () {
    const colorOptions = document.querySelectorAll(".color-option");
    const sizeOptions = document.querySelectorAll(".size-option");
    const variantIdInput = document.getElementById("selectedVariantId");
    const colorIdInput = document.getElementById("selectedColorId");
    const sizeIdInput = document.getElementById("selectedSizeId");

    function updateVariantId() {
        let selectedColorId = document.querySelector('input[name="colorOptions"]:checked')?.getAttribute("data-color");
        let selectedSizeId = document.querySelector('input[name="sizeOptions"]:checked')?.getAttribute("data-size");

        if (selectedColorId && selectedSizeId) {
            document.querySelectorAll(".color-option").forEach((colorOption) => {
                if (
                    colorOption.getAttribute("data-color") === selectedColorId &&
                    colorOption.getAttribute("data-size") === selectedSizeId
                ) {
                    variantIdInput.value = colorOption.getAttribute("data-variant");
                    colorIdInput.value = selectedColorId;
                    sizeIdInput.value = selectedSizeId;
                }
            });
        }
    }

    colorOptions.forEach((option) => option.addEventListener("change", updateVariantId));
    sizeOptions.forEach((option) => option.addEventListener("change", updateVariantId));

    updateVariantId();
});


function updateVariantId() {
    let selectedColorId = document.querySelector('input[name="colorOptions"]:checked')?.getAttribute("data-color");
    let selectedSizeId = document.querySelector('input[name="sizeOptions"]:checked')?.getAttribute("data-size");

    if (selectedColorId && selectedSizeId) {
        document.querySelectorAll(".color-option").forEach((colorOption) => {
            if (
                colorOption.getAttribute("data-color") === selectedColorId &&
                colorOption.getAttribute("data-size") === selectedSizeId
            ) {
                document.getElementById("selectedVariantId").value = colorOption.getAttribute("data-variant");
                document.getElementById("selectedColorId").value = selectedColorId;
                document.getElementById("selectedSizeId").value = selectedSizeId;
            }
        });
    }
}
