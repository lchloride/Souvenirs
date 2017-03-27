/**
 * 
 */


function calcSize() {
	//display_width is the width(px) of active and available for showing contnet window 
	var display_width = window.innerWidth
			|| document.documentElement.clientWidth
			|| document.body.clientWidth;
	var r = (display_width >= 1367 ? display_width * 0.8
			: (display_width >= 768 ? display_width * 0.9
					: display_width - 10));
	var mainbody_width = r - 50 * 2;
	// Calculate size of latest pictures
	var latest_panel_width = $('#latest_picture').width() - 20;
	var image_width_margin = $('div.img').outerWidth(true)-$('div.img a img').width();
	var picture_width = Math.round(latest_panel_width / 2 - image_width_margin)-1;
	var picture_height = picture_width;
	for (i = 1; i<=4; i++) {
		$('#picture_item_img_'+i).css('width', picture_width);
		$('#picture_item_img_'+i).css('height', picture_height);
	}
	$('#latest_picture .desc').css('width', picture_width);
	
	// Calculate size of templates
	var templates_width = $('.templates').width();
	var template_width = Math.round(templates_width / 3 - image_width_margin);
	var template_height = template_width * 4 / 3;
	for (i = 1; i <= 8; i++) {
		document.getElementById("template" + i).width = template_width;
		document.getElementById("template" + i).height = template_height;
	}	
	
	// Calculate size of albums list
	var albums_info = Math.max($('#left_col').outerHeight(true), $('#center_col').outerHeight(true));
	var col_margin_top = $('#albums_info').outerHeight(true) - $('#albums_info').height() + 20;
	$('#albums_info').css('height', albums_info-col_margin_top);
	$('#personal_albums_list').css('height', (albums_info - $('h4.title').outerHeight(true) - $('#myTab').outerHeight(true)-$('#create_btn').outerHeight(true)-col_margin_top));
	$('#shared').css('height', (albums_info - $('h4.title').outerHeight(true) - $('#myTab').outerHeight(true)));
	var album_cover_width = $('#personal_albums_list').width() - 15 - image_width_margin;
	$('#personal_albums_list img').css('width', album_cover_width);
	$('#personal_albums_list img').css('height', album_cover_width);
	$('#shared img').css('width', album_cover_width);
	$('#shared img').css('height', album_cover_width);
	
	//Calculate chart of pictures height
	$('#pic_chart').css('height', Math.round(palbum_list_obj.length*50*modify(palbum_list_obj.length)));
}

function modify(x) {
	return 8/(x+3);
}

function displayFilename() {
	var filepath = $('#upload_file').val();
	$('#filename_display').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
	$('#filename').val(filepath.substring(filepath.lastIndexOf('\\') + 1));
}

function drawAlbumPie(){
	 
	palbum_count = palbum_list_obj.length;
	salbum_count = salbum_list_obj.length;
    data1 = [[['Personal', palbum_count],['Shared', salbum_count]]];
    toolTip1 = ['Personal Albums', 'Shared Albums'];
 	dataLabel = [palbum_count+' ('+Math.round(palbum_count/(palbum_count+salbum_count)*1000)/10+'%)', 
 	             salbum_count+' ('+Math.round(salbum_count/(palbum_count+salbum_count)*1000)/10+'%)']
    var plot1 = jQuery.jqplot('album_chart', 
        data1,
        {
            title: 'Albums Distribution\nTotal '+(palbum_count+salbum_count)+' Albums', 
            seriesDefaults: {
                shadow: false, 
                renderer: jQuery.jqplot.PieRenderer, 
                rendererOptions: { padding: 2, sliceMargin: 2, showDataLabels: true, dataLabels: dataLabel }
            },
            legend: {
                show: true,
                location: 'e',
                renderer: $.jqplot.EnhancedPieLegendRenderer,
                rendererOptions: {
                    numberColumns: 1,
                    toolTips: toolTip1
                }
            },
        }
    );
}

function drawPicturesBar(){
	data = new Array();
	ticks = new Array();
	for (var i=0; i<palbum_count; i++) {
		data.push(new Array(palbum_list_obj[i].pictures_count, i+1));
		ticks.push(palbum_list_obj[i].album_name);
	}
    plot1 = $.jqplot('pic_chart', [data], {
        captureRightClick: true,
        title: 'Personal Pictures Distribution', 
        seriesDefaults:{
            renderer:$.jqplot.BarRenderer,
            shadowAngle: 135,
            rendererOptions: {
                barDirection: 'horizontal',
                highlightMouseDown: true,
                varyBarColor: true
            },
            pointLabels: {show: true, formatString: '%d'}
        },
        legend: {
            show: false,
            location: 'e',
            placement: 'outside'
        },
        axes: {
            yaxis: {
                renderer: $.jqplot.CategoryAxisRenderer,
                ticks: ticks
            }
        }
    }); 
}

function useDefaultCover() {
	if ($('#default_picture').is(':checked')) {
		$('#upload_file').attr('disabled', 'disbaled');
		$('#upload_file_btn').addClass('disabled');
	} else {
		$('#upload_file').removeAttr('disabled');
		$('#upload_file_btn').removeClass('disabled');
	}
}

function activate(idx){
	$("#latest_picture .img").css('height', "auto");
	var height = $('#img_'+idx).outerHeight();
	//alert(height);
	var i=1;
	$("#latest_picture .img").css('height', height);
}
function normalize(idx){
	calcSize();
}

function checkSubmit() {
	if ($('#album_name').val().length > 60) {
		alert("Album name should be less than 60 characters.");
		return false;
	}
	if ($('#album_name').val().length == 0) {
		alert("Album name cannot be empty.");
		return false;
	}
	if ($('#description').val().length > 200) {
		alert("Description should be less than 200 characters.");
		return false;
	}
	if (($('#filename').val()=="" || $('#filename').val()==undefined || $('#filename').val()==null) && !$('#default_cover').is(':checked')) {
		alert("You must select a cover from local OR use default cover.");
		return false;
	}
	return true;
}
