/**
 * 
 */
	function queryPictureInAlbum(album_name) {
		ajaxProcess(drawImageList, "/Souvenirs/AlbumAjax?album_identifier="+album_name+"&range=personal");
	}
	
	function drawImageList(image_json) {
		image_obj = JSON.parse(image_json);
		var image_html = "";
		if (image_obj.length == 0)
			image_html = "<tr><td><h4>No Image In this Album!</h4></td></tr>";
		for (i=0; i<image_obj.length; i++)
			image_html +=		'<tr>'+
				'<td style="border-top: 1px solid #bbb;"><input type="checkbox" value="" id="checkbox_'+i+'"></td>'+
				'<td style="border-top: 1px solid #bbb;">'+
					'<div class="media">'+
						'<a class="pull-left" href="#"> <img class="media-object" src="'+image_obj[i].Addr+'" alt="'+image_obj[i].Filename+'" width="120" height="">'+
						'</a>'+
						'<div class="media-body">'+
							'<h4 class="media-heading">'+image_obj[i].Filename+'</h4>'+
							'<p>'+
								'Album name: '+image_obj[i].AlbumName+'<br> '+
								'Description: '+image_obj[i].Description+'<br>'+
								'Uploaded Time: '+image_obj[i].UploadTime+'<br>'+
							'</p>'+
						'</div>'+
					'</div>'+
				'</td>'+
			'</tr>';
		document.getElementById("image_list_table_body").innerHTML = image_html;
	}
	
	function addPictures() {
		for (i=0; i<image_obj.length; i++) {
			if (document.getElementById("checkbox_"+i).checked) {
				var item = {album_name:image_obj[i].AlbumName, filename:image_obj[i].Filename, addr:image_obj[i].Addr};
				var compare_flag = false;
				for (j=0; j<selected_image.length; j++)
					if (cmpSelectedImageItem(selected_image[j], item)) {
						compare_flag = true;
						break;
					}
				if (!compare_flag) {
					selected_image.push(item);
					$.bootstrapGrowl("Add "+image_obj[i].Filename+" in Album "+image_obj[i].AlbumName+" successfully.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
				}else
					$.bootstrapGrowl(image_obj[i].Filename+" in Album "+image_obj[i].AlbumName+" has already existed.", { type: 'danger' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}});
			}
		}
		
		displaySelectedPicture();
	}
	
	function displaySelectedPicture() {
		var image_html = "";
		document.getElementById("share_list_num").innerHTML = selected_image.length;
		for (i=0; i<selected_image.length; i++)
			image_html += 	'<tr>'+
				// '<td><input type="checkbox" value=""></td>'+
				'<td style="border-top: 1px solid #bbb;">'+selected_image[i].album_name+'</td>'+
				'<td style="border-top: 1px solid #bbb;">'+selected_image[i].filename+'</td>'+
				'<td style="border-top: 1px solid #bbb;"><div id="large"></div>'+
				'<button type="button" class="btn btn-link " style="padding: 0px" onmouseover="mouseover(\''+selected_image[i].addr+'\')" onmouseout="mouseout()">Preview</button>'+
				' | <button type="button" class="btn btn-link " style="padding: 0px" onclick="removeSelectedImage('+i+')">Remove</button></td>'+
				'</td>'	
				'</tr>';
		image_html += '<tr><td></td><td></td><td></td><td></td></tr>';
		document.getElementById("selected_image_table_body").innerHTML = image_html;
	}
	
	function cmpSelectedImageItem(item1, item2) {
		if (item1.album_name == item2.album_name && item1.filename == item2.filename)
			return true;
		else
			return false;
	}
	
	function mouseover(addr) {
		var ei = $("#large");  
		 ei.html(
			'<img style="border:1px solid gray;position:absolute;right:0px;top:0px;z-index:1;max-width:400px;" src="'+addr+'"/>').show();  
	}
	
	function mouseout(addr) {
		var ei = $("#large");  
		ei.hide();
	}
	
	function removeSelectedImage(idx) {
		var len = selected_image.length;
		for (i=0; i<len; i++) {
			var item = selected_image.shift();
			if (idx != i)
				selected_image.push(item);
		}
		displaySelectedPicture();
	}
	
	function selectImageListAll() {
		for (i=0; i<image_obj.length; i++)
			document.getElementById("checkbox_"+i).checked="checked";
	}

	function sharePictures(group_id) {
		var share_list_obj = new Array();
		for (var i = 0; i < selected_image.length; i++) {
			var item = {album_name:selected_image[i].album_name, filename:selected_image[i].filename};
			share_list_obj.push(item);
		}
		var share_list_json = JSON.stringify(share_list_obj);
		ajaxProcess(sharePicturesCallback, "sharePictures", "list_json="+share_list_json+"&group_id="+group_id);
		// URL:/Souvenirs/sharePictures 
		//POST: [{"list_json", %share_list_json%}, {"group_id", %Group_id%}] 注：POST参数表的一种表达形式，实际传输的格式不是这个样子
		// 等价于
		// 一个form: 
		//  	input_text: 名字是list_json, 内容是%share_list_json%
		//     input_text: 名字是group_id，内容是 %Group_id%
	}

	function sharePicturesCallback (result) {
		if (result.indexOf('{')!=0)
			$.bootstrapGrowl("Sharing pictures failed. Error: "+result, { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}});
		else {
			var result_obj = JSON.parse(result);
			var success_list_obj = result_obj.success_list;
			var failure_list_obj = result_obj.failure_list;
			var duplication_list_obj = result_obj.duplication_list;

			for (var i = 0; i < success_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl("Sharing "+success_list_obj[i]+" succeed.", { type: 'success' , delay:2000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
			
			for (var i = 0; i < failure_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl("Sharing "+failure_list_obj[i]+" failed.", { type: 'danger' , delay:4000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
			for (var i = 0; i < duplication_list_obj.length; i++) {
				setTimeout($.bootstrapGrowl(duplication_list_obj[i]+" has already existed.", { type: 'info' , delay:3000, offset: {from: 'top', amount: MSG_OFFSET}}), 1000);
			}
		}
	}
