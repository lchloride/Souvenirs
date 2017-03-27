/**
 * 
 */
	function activate(idx) {
		if (mouse_over_idx>0){
			if (is_personal) {
				document.getElementById("img_edit_btn_"+mouse_over_idx).style.display = "none";
				document.getElementById("img_delete_btn_"+mouse_over_idx).style.display = "none";
			} else {
				document.getElementById("img_details_btn_"+mouse_over_idx).style.display = "none";
			}
		}
		var i=1;
		while (document.getElementById("img_"+i)!=null) {
			document.getElementById("img_"+i).style.height = "auto";
			i++;
		}
		mouse_over_idx = idx;

		if (is_personal) {
			document.getElementById("img_edit_btn_"+idx).style.display = "inline";
			document.getElementById("img_delete_btn_"+idx).style.display = "inline";
		} else {
			document.getElementById("img_details_btn_"+mouse_over_idx).style.display = "inline";
		}
		
		var height = $('#img_'+idx).outerHeight();
		//alert(height);
		var i=1;
		while (document.getElementById("img_"+i)!=null) {
			document.getElementById("img_"+i).style.height = height+"px";
			i++;
		}
	}
	
	function normalize() {
		document.getElementById("img_edit_btn_"+mouse_over_idx).style.display = "none";
		document.getElementById("img_delete_btn_"+mouse_over_idx).style.display = "none";
	}
	
	function saveAlbumName() {
		var new_album_name = document.getElementById("album_name").value;
		ajaxProcess(saveAlbumNameCallback, "/Souvenirs/updateAlbumName?old_name="+encodeURIComponent(original_album_name)+
				"&new_name="+encodeURIComponent(new_album_name)+"&is_personal="+is_personal+"&group_id="+group_id);
	}
	
	function saveAlbumNameCallback(result){
		//alert(result);
		if (result.indexOf('true')>-1) {
//			$.bootstrapGrowl("Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
			if (is_personal) {
				location.href="/Souvenirs/album?album_name="+encodeURIComponent(document.getElementById("album_name").value)+"&update=true";
			} else {
				location.href="/Souvenirs/sharedAlbum?group_id="+group_id+"&update=true";
			}
		}
		else
			$.bootstrapGrowl("Failed. Error:"+result, { type: 'danger' , offset: {from: 'top', amount: MSG_OFFSET}});
	}
	
	function saveDescription() {
		var new_description =  document.getElementById("description").value;
		ajaxProcess(saveDescriptionCallback, "/Souvenirs/updateDescription?album_name="+encodeURIComponent(original_album_name)+
				"&new_description="+encodeURIComponent(new_description)+"&is_personal="+is_personal+"&group_id="+group_id);
	}
	
	function saveDescriptionCallback(result){
		//alert(result);
		if (result.indexOf('true')>-1) {
			$.bootstrapGrowl("Success.", { type: 'success' , offset: {from: 'top', amount: MSG_OFFSET}});
		}
		else
			$.bootstrapGrowl("Failed. Error:"+result, { type: 'danger' , offset: {from: 'top', amount: MSG_OFFSET}});
	}
	
	function deletePicture(idx) {
		var filename =  document.getElementById("image_item_text_"+idx).innerHTML;
		var msg = "Do you really want to delete this picture? Deleted one cannot be recovered!\n\nPlease comfirm!"; 
		if (confirm(msg)==true){ 
			ajaxProcess(deletePictureCallback, "/Souvenirs/deletePicture?album_name="+encodeURIComponent(original_album_name)+
					"&filename="+encodeURIComponent(filename))+"&is_personal="+is_personal;
		}else{ 
			$.bootstrapGrowl("Deletion is aborted.", { type: 'info' , offset: {from: 'top', amount: MSG_OFFSET}});
		} 
		
	}
	
	function deletePictureCallback(result) {
		if (result.indexOf('true')>-1) {
//			$.bootstrapGrowl("Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
			location.href="/Souvenirs/album?album_name="+encodeURIComponent(document.getElementById("album_name").value)+"&update=true";
		}
		else {
			$.bootstrapGrowl("Failed. Error:"+result, { type: 'danger' , offset: {from: 'top', amount: MSG_OFFSET}});
		}
	}
	
	function confirmDelete(Album_name) {
		  var r=confirm("Are you sure to delete this album?\n\nAll pictures and related information will be deleted and cannot be recovered!\n\nPlease confirm!")
		  if (r==true)
		    {
		    	ajaxProcess(deleteAlbumCallback, "/Souvenirs/deletePAlbum?album_name="+encodeURIComponent(Album_name));
		    }
		  else
		    {
			  $.bootstrapGrowl("Deletion is canceled.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}});
		    }
	}
	
	function deleteAlbumCallback(result) {
		if (result.trim()=="true") {
			location.href="/Souvenirs/homepage?Upload_result=true";
		} else {
			$.bootstrapGrowl("Deletion failed. Error:"+result, { type: 'danger' , offset: {from: 'top', amount: MSG_OFFSET}});
		}
	}