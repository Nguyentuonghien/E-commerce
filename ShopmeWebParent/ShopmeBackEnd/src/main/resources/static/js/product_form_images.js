
 var extraImagesCount = 0;
 
 $(document).ready(function() {
	 $("input[name='extraImage']").each(function(index) {
	     extraImagesCount++;
	     
	     $(this).change(function() {
	         // checkFileSize mà false -> return, còn checkFileSize mà true -> show image thumbnail của file ảnh đó lên 
    	     if (!checkFileSize(this)) {
    	         return;
    	     }
	         showExtraImageThumbnail(this, index);
	     });
     }); 	
     
     $("a[name='linkRemoveExtraImage']").each(function(index) {
         $(this).click(function() {
             removeExtraImage(index);
         });
     });
 });
 
 // show anh phu len  
 function showExtraImageThumbnail(fileInput, index) {
     // doi tuong file dau tien
     var file = fileInput.files[0];   
     
     fileName = file.name;
     imageNameHiddenField = $("#imageName" + index);
     
     if (imageNameHiddenField.length) {
         imageNameHiddenField.val(fileName);
     }
     
     var reader = new FileReader();
     reader.onload = function(e) {
    	 $("#extraThumbnail" + index).attr("src", e.target.result);
     };   	 
     reader.readAsDataURL(file);
     
     // extra image đầu tiên ứng với index = 0
     if (index >= extraImagesCount - 1) {
         addNextExtraImageSection(index + 1);
     }
 }
 

 function addNextExtraImageSection(index) {
     // thêm form ảnh
     htmlExtraImage = `
         <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}"><label>Extra Image #${index + 1}:</label></div>
            <div class="m-2">
                <img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" 
                       src="${defaultImageThumbnailSrc}" class="img-fluid" />
            </div>
            <div>
                <input type="file" name="extraImage" accept="image/png, image/jpeg" 
                       onchange="showExtraImageThumbnail(this, ${index})" />
            </div>    
        </div>
     `;
     
     // xử lý delete button
     htmlLinkRemove = `
          <a class="btn fas fa-times-circle fa-2x icon-dark float-right" 
             href="javascript:removeExtraImage(${index - 1})"
             title="Remove this image"></a>
     `;
     
     // nối code HTML vào phần tử có id=divProductImages     
     $("#divProductImages").append(htmlExtraImage);
     
     $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);
     
     extraImagesCount++;
 }
  
 function removeExtraImage(index) {
     $("#divExtraImage" + index).remove();
 }
     
     
     