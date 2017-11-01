<!-- JS Scripts-->
<!-- jQuery Js -->
<script src="${base}/assets/js/jquery-1.10.2.js"></script>
<!-- Bootstrap Js -->
<script src="${base}/assets/js/bootstrap.min.js"></script>
	 
<!-- Metis Menu Js -->
<script src="${base}/assets/js/jquery.metisMenu.js"></script>

<script src="${base}/assets/js/jquery.form.js"></script>

<!-- Custom Js -->
<script src="${base}/assets/js/custom-scripts.js"></script>


<script>
function getFormJson(frm) {  //frm：form表单的id
    var o = {};  
    var a = $("#"+frm).serializeArray();  
    $.each(a, function() {  
        if (o[this.name] !== undefined) {  
            if (!o[this.name].push) {  
                o[this.name] = [ o[this.name] ];  
            }  
            o[this.name].push(this.value || '');  
        } else {  
            o[this.name] = this.value || '';  
        }  
    });  
    return o;  
}  
</script>
	 
      
