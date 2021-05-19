 $(document).ready(function() {
    $("#buttonCancel").on("click", function() {
    	window.location = moduleURL;
    });
    	
    $("#fileImage").change(function() {
    
        // checkFileSize mà false -> return, còn checkFileSize mà true -> show image thumbnail của file ảnh đó lên 
    	if (!checkFileSize(this)) {
    	    return;
    	}
    	showImageThumbnail(this);
    }); 	
});    
     
function checkFileSize(fileInput) {
    // lay ra size cua file anh duoc upload
	fileSize = fileInput.files[0].size;
		
	// kich thuoc file anh khong duoc > MAX_FILE_SIZE
	if(fileSize > MAX_FILE_SIZE) {
		fileInput.setCustomValidity("You must choose an image  less than " + MAX_FILE_SIZE + " bytes!");
		fileInput.reportValidity();
		
		return false;
	} else{
		fileInput.setCustomValidity("");
		
		return true;
	}
}     
     
function showImageThumbnail(fileInput) {
    // doi tuong file dau tien
    var file = fileInput.files[0];   
    var reader = new FileReader();
    reader.onload = function(e) {
    	$("#thumbnail").attr("src", e.target.result);
    };   	 
    reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
} 
     
function showErrorModal(message) {
    showModalDialog("Error", message);
}
     
function showWarningModal(message) {
    showModalDialog("Warning", message);
}
     
     
     
     