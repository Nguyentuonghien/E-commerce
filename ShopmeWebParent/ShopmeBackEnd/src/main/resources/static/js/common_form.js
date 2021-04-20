 $(document).ready(function() {
    $("#buttonCancel").on("click", function() {
    	window.location = moduleURL;
    });
    	
    $("#fileImage").change(function() {
    	// lay ra size cua file anh duoc upload
    	fileSize = this.files[0].size;
    		
    	// kich thuoc file anh khong duoc > 1 MB = 1024^2 B = 1048576 B
    	if(fileSize > 1048576) {
    		this.setCustomValidity("You must choose an image  less than 1MB!");
    		this.reportValidity();
    	} else{
    		this.setCustomValidity("");
    		showImageThumbnail(this);
    	}
    }); 	
});    
     
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
     
     
     
     