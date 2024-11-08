document.addEventListener("DOMContentLoaded", function () {
  const colorOptions = document.querySelectorAll(".color-option");
  const sizeOptions = document.querySelectorAll(".size-option");
  const variantIdInput = document.getElementById("selectedVariantId");
  const colorIdInput = document.getElementById("selectedColorId");
  const sizeIdInput = document.getElementById("selectedSizeId");

  function updateVariantId() {
    const selectedColorId = document
      .querySelector('input[name="colorOptions"]:checked')
      ?.getAttribute("data-color");
    const selectedSizeId = document
      .querySelector('input[name="sizeOptions"]:checked')
      ?.getAttribute("data-size");

    if (selectedColorId && selectedSizeId) {
      // Tìm productVariantId dựa trên màu sắc và kích thước đã chọn
      const matchingVariant = Array.from(
        document.querySelectorAll(".variant-option")
      ).find(
        (option) =>
          option.getAttribute("data-color-id") === selectedColorId &&
          option.getAttribute("data-size-id") === selectedSizeId
      );

      if (matchingVariant) {
        variantIdInput.value = matchingVariant.getAttribute("data-variant-id");
        colorIdInput.value = selectedColorId;
        sizeIdInput.value = selectedSizeId;
        console.log("Selected Variant ID:", variantIdInput.value);
      } else {
        variantIdInput.value = "";
      }
    }
  }

  colorOptions.forEach((option) =>
    option.addEventListener("change", updateVariantId)
  );
  sizeOptions.forEach((option) =>
    option.addEventListener("change", updateVariantId)
  );
});
