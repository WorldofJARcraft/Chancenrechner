function writeSpecialLinks()
{
	if (!document.getElementsByTagName) {return;}
	var links = document.getElementsByTagName("A");
	
	for (var i=0; i < links.length; i++) {
		var thisLink = links[i];
		var linkType = thisLink['className'];

		if (linkType)
		{
			if (linkType == "newwindow")
			{
//				thisLink.title = "öffnet neues Fenster";
				thisLink.setAttribute("title", "öffnet neues Fenster");
				thisLink.target = "_blank";
				if (thisLink.firstChild) {
					thisLink.firstChild.title = "im neuen Fenster";
				}
			}
			if (linkType == "imgzoom") 
			{
				var linkZiel = thisLink.getAttribute("href");
				thisLink.onclick=function(){return(popup(this,700,650));};
				thisLink.title = "Bildvergrößerung im neuen Fenster";

			/*	alert(linkZiel); */
			}
		}
	}
}


function popup(o,winHeight,winWidth)
{
// open a new window with the location of the link's href
	var strOptions="height="+winHeight+",width="+winWidth;
	window.open(o.href,'bildpopup',strOptions);
	return false;
}

/*
	if(document.images) {
		popup = window.open("/popup.php?imgFn="+URL,"image",strOptions);
		popup.focus();

*/

// Skript initialisieren - nur in DOM-fähigen Browsern 

if(document.getElementById && document.createTextNode) {
	window.onload = writeSpecialLinks;
}

