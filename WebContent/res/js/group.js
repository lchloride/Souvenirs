/**
 * 
 */
function displayMyGroupTable(json) {
	var str="";
	var my_group_list_json = json;
	my_group_list_obj = JSON.parse(my_group_list_json);
	var line = "";
	for (var i=0; i<my_group_list_obj.length; i++) {
		line = '<tr><td>'+my_group_list_obj[i].group_id+'</td><td>'+my_group_list_obj[i].group_name+'</td><td>'+my_group_list_obj[i].intro+'</td>'+
		'<td><button type="button" class="btn btn-link " style="padding: 0px" onclick="editGroup('+i+')">Edit</button> | '+
		'<button type="button" class="btn btn-link " style="padding: 0px" onclick="leaveGroup('+i+')">Leave</button> | '+
		'<button type="button" class="btn btn-link " style="padding: 0px" ><a href="/Souvenirs/sharedAlbum?group_id='+my_group_list_obj[i].group_id+'">Show Attached Album<a></button>'+
		'</td>	</tr>';
		str += line;
	}
	$('#my_group_table_body').html(str);
	
}

function changeMGContentLength(length) {
	ajaxProcess(changeMGContentLengthCallback, "/Souvenirs/showMyGroup?page_number="+my_group_active_page+"&content_length="+length);
}

function changeMGContentLengthCallback(result) {
	displayMyGroupTable(result);
	var current_length = document.getElementById("MG_content_length").value;
	my_group_active_page = Math.round((my_group_active_page-1) * previous_length / current_length)+1; 
	my_group_total_page = Math.ceil(my_group_total_items / current_length);
	previous_length = current_length;
	pagination('my_group_pagination', my_group_active_page, my_group_total_page);
}

function changeMGPage(page) {
	my_group_active_page = page;
	ajaxProcess(changeMGPageCallback, "/Souvenirs/showMyGroup?page_number="+page+"&content_length="+document.getElementById("MG_content_length").value);
}

function changeMGPageCallback(result) {
	displayMyGroupTable(result);
	var current_length = document.getElementById("MG_content_length").value;
	my_group_total_page = Math.ceil(my_group_total_items / current_length);
	pagination('my_group_pagination', my_group_active_page, my_group_total_page);
}

function editGroup(idx) {
	$('#edit_group_id').val(my_group_list_obj[idx].group_id );
	$('#edit_group_name').val(my_group_list_obj[idx].group_name.replace(/&apos;/g, "'"));
	$('#edit_intro').html(my_group_list_obj[idx].intro);
	$('#editModal').modal('show');
}

function submitEdit() {
	var group_id = encodeURIComponent($("#edit_group_id").val());
	var group_name = encodeURIComponent($("#edit_group_name").val());
	var intro = encodeURIComponent($("#edit_intro").val());
	ajaxProcess(editCallback, "/Souvenirs/updateGroup?group_id="+group_id+"&group_name="+group_name+"&intro="+intro)		
}

function editCallback(result) {
	$('#editModal').modal('toggle');
	var result_obj = JSON.parse(result);
	if (result_obj.length == 1) {
		if (result_obj[0].result == "no item changed") 
			$.bootstrapGrowl("No item changed.", { type: 'info' , offset: {from: 'top', amount: 50}});
		else
			$.bootstrapGrowl("Error: "+result_obj[i].item+", "+result_obj[i].result, { type: 'danger' , offset: {from: 'top', amount: 50}});
	}else {
		for (var i=0; i<result_obj.length-1; i++) {
			if (result_obj[i].result == "true") {
				$.bootstrapGrowl("Update "+result_obj[i].item+" succeeded.", { type: 'success' , offset: {from: 'top', amount: 50}});
			} else {
				$.bootstrapGrowl("Error: "+result_obj[i].item+", "+result_obj[i].result, { type: 'danger' , offset: {from: 'top', amount: 50}});
			}
		}
		displayMyGroupTable(result_obj[result_obj.length-1]);
	}
}

function leaveGroup(idx) {
	var r=confirm("Are you sure to leave this group? You will not be able to see any shared pictures after leaving.");
	if (r==true)
	{
		var group_id = encodeURIComponent(my_group_list_obj[idx].group_id);
		ajaxProcess(leaveGroupCallback, "/Souvenirs/leaveGroup?group_id="+group_id);
	}else
	{
		$.bootstrapGrowl("Operation canceled.", { type: 'info' , offset: {from: 'top', amount: 50}});
	}
}

function leaveGroupCallback(result) {
	if (result.trim() == "true") {
		ajaxProcess(changeMGPageCallback, "/Souvenirs/showMyGroup");
		$.bootstrapGrowl("Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
	}else
		$.bootstrapGrowl("Error: "+result, { type: 'danger' , offset: {from: 'top', amount: 50}});	
}

function searchGroup() {
	var keyword = $('#search_id').val();
	var method = $('#search_method').val()=="Fuzzy Search"?"true":"false";
	ajaxProcess(searchGroupCallback, "/Souvenirs/searchGroup?keyword="+keyword+"&is_fuzzy="+method);
}

function searchGroupCallback(result)  {
	displaySearchResult(result);
}

function displaySearchResult(json) {
	search_result_obj = JSON.parse(json);
	if (search_result_obj.length == 0)
		$('#search_table_body').html("No matched group is found.");
	else {
		var str = "";
		var line = "";
		for (var i=0; i<search_result_obj.length; i++) {
			line = '<tr><td>'+search_result_obj[i].group_id+'</td><td>'+search_result_obj[i].group_name+'</td><td>'+search_result_obj[i].intro+'</td>'+
			'<td><button type="button" class="btn btn-link " style="padding: 0px" onclick="joininGroup('+i+')">Join in</button> '+
			'</td>	</tr>';
			str += line;
		}
		$('#search_table_body').html(str);
	}
}

function joininGroup(idx) {
	ajaxProcess(joininGroupCallback, "/Souvenirs/joininGroup?group_id="+search_result_obj[idx].group_id);
}

function joininGroupCallback(result) {
	if (result.trim() == "true") {
		$.bootstrapGrowl("Join In Success.", { type: 'success' , offset: {from: 'top', amount: 50}});
	} else {
		$.bootstrapGrowl(result, { type: 'danger' , offset: {from: 'top', amount: 50}});
	}
		
}

function createGroup() {
	$('#create_form').submit();		
}

function useDefaultCover() {
	if ($('#default_cover').is(':checked')) {
		$('#upload_file').attr('disabled', 'disbaled');
		$('#upload_file_btn').addClass('disabled');
	} else {
		$('#upload_file').removeAttr('disabled');
		$('#upload_file_btn').removeClass('disabled');
	}
}

function displayFilename() {
	var filepath = $('#upload_file').val();
	$('#filename_display').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
	$('#filename').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
}

function jumpMGPage() {
	var page = $('#jump_MGpage').val();
	my_group_active_page = page;
	ajaxProcess(jumpMGPageCallback, "/Souvenirs/showMyGroup?page_number="+page+"&content_length="+document.getElementById("MG_content_length").value);
}

function jumpMGPageCallback(result) {
	displayMyGroupTable(result);
	var current_length = document.getElementById("MG_content_length").value;
	my_group_total_page = Math.ceil(my_group_total_items / current_length);
	pagination('my_group_pagination', my_group_active_page, my_group_total_page);
}