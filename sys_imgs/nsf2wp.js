	var MAX_FEEDS = 0;
	function getfeed( url ){
		if( url.indexOf( "?" ) > -1 ){
			url += "&feed=rss2";
		}else{
			if( !url.endsWith( "/" ) ){
				url += "/";
			}
			url += "?feed=rss2";
		}
		$.get( url, {}, function( xml ){
			try{
				var channel = ( xml.getElementsByTagName( "channel" ) )[0];
				var items = channel.getElementsByTagName( "item" );
				var n = items.length;
				if( MAX_FEEDS > 0 && n > MAX_FEEDS ){
					n = MAX_FEEDS;
				}
				var ul = "<ul>";
				for( i = 0; i < n; i ++ ){
					var item = items[i];
					var title0 = ( item.getElementsByTagName( "title" ) )[0];
					var title = title0 ? title0.childNodes[0].nodeValue : "";
					var link0 = ( item.getElementsByTagName( "link" ) )[0];
					var link = link0 ? link0.childNodes[0].nodeValue : "";
					var category0 = ( item.getElementsByTagName( "category" ) )[0];
					var category = category0 ? category0.childNodes[0].nodeValue : "";
					var pubdate0 = ( item.getElementsByTagName( "pubDate" ) )[0];
					var pubDate = pubdate0 ? pubdate0.childNodes[0].nodeValue : "";

					try{
						var dt = new Date( pubDate );
						var y = dt.getFullYear();
						var m = dt.getMonth() + 1;
						var d = dt.getDate();
						pubDate = y + "." + ( ( m < 10 ) ? "0" : "" ) + m + "." + ( ( d < 10 ) ? "0" : "" ) + d;
					}catch( e ){
					}

					var li = "<li><span>" + pubDate + "&nbsp;&nbsp;:<a href=\"" + link + "\">" + title + "</a></li>";
					ul += li;
				}
				ul += "</ul>";
				$('#category').html( ul );
			}catch( e ){
			}
		});
	}


