/* 	Twitter Friends v1.0
	Blog : http://www.moretechtips.net
	Project: http://code.google.com/p/twitter-friends-widget/
	Date: 2011-06-09
	Copyright 2009 [Mike @ moretechtips.net] 
	Licensed under the Apache License, Version 2.0 
	(the "License"); you may not use this file except in compliance with the License. 
	You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
*/
(function(d){d.fn.twitterFriends=function(o){var s={debug:0,username:"",friends:0,users:18,users_max:100,loop:0,user_link:0,user_image:48,user_animate:"opacity",user_change:200,user_swap:5E3,user_append:1,header:"",tweet:0,tweet_avatar:1,tweet_author:0,tweet_date:1,tweet_source:1,tweet_image:48,tweet_stay:5E3,tweet_change:200,tweet_animate:"opacity",info:'<div class="tf-info"><a title="get Twitter Friends & Followers Widget!" target="_blank" href="http://www.moretechtips.net/">.i</a></div>'};o=d.extend({},
s,o);return this.each(function(){var t=0,f=[],i=-1,k=-1,l=-1,g=d(this),j=null,m=null,p=null,b=o;if(g.attr("options")){try{b=eval("("+g.attr("options")+")")}catch(B){g.html('<b style="color:red">'+B+"</b>");return}b=d.extend({},s,b)}var q=function(){if((i+1)*b.users>=f.length)if(b.loop)i=-1;else return;j.html("");i++;for(var a=i*b.users;a<(i+1)*b.users;a++){if(a>=f.length)break;var c=f[a],e=b.user_link&&c.url?c.url:"http://twitter.com/"+c.screen_name,h=c.name+(c.status&&b.tweet?": "+c.status.text:
"");h=h.replace(/"/g,"&quot;").replace(/'/g,"&#39;");d('<a style="display:none;height:'+b.user_image+'px" href="'+e+'" title="'+h+'"><img src="'+c.profile_image_url+'" border="0" height="'+b.user_image+'" width="'+b.user_image+'"/></a>').appendTo(j)}k=b.user_append?-1:d("a",j).length;u();l=-1;b.tweet&&v()},u=function(){k=b.user_append?k+1:k-1;var a=d("a:eq("+k+")",j);if(a.length){var c={};c[b.user_animate]="show";a.animate(c,b.user_change,"linear",u)}else b.tweet||j.animate({opacity:1},b.user_swap,
"linear",q)},v=function(){l>-1?d("div",m).fadeOut(b.tweet_change,w):w()},w=function(){for(var a=null,c=null;!c;){l++;if(l>=d("a",j).length){q();return}a=f[i*b.users+l];c=a.status}a=a;c=c;var e=b.user_link&&a.url?a.url:"http://twitter.com/"+a.screen_name,h=a.name;m.html('<div style="display:none;">'+(b.tweet_avatar?'<span class="tf-avatar"><a href="'+e+'" title="'+h+'"><img src="'+a.profile_image_url+'" height="'+b.tweet_image+'" width="'+b.tweet_image+'" border="0"/></a></span>':"")+'<span class="tf-body">'+
(b.tweet_author?'<strong><a href="'+e+'" title="'+h+'">'+a.screen_name+"</a></strong>":"")+'<span class="tf-content">'+C(c.text)+'</span><span class="tf-meta">'+(b.tweet_date?'<a class="tf-date" href="http://twitter.com/'+a.screen_name+"/status/"+c.id+'">'+D(c.created_at)+"</a>":"")+(b.tweet_source?'<span class="tf-source"> from '+c.source.replace(/&lt;/gi,"<").replace(/&gt;/gi,">").replace(/&quot;/gi,'"')+"</span>":"")+"</span></span></div>");a={};a[b.tweet_animate]="show";d("div",m).animate(a,b.tweet_change,
"linear",E)},E=function(){d("div",m).animate({opacity:1},b.tweet_stay,"linear",v)},C=function(a){return a.replace(/\bhttps?\:\/\/\S+/gi,function(c){var e="";c=c.replace(/(\.*|\?*|\!*)$/,function(h,r){e=r;return""});return'<a class="tf-link" href="'+c+'">'+(c.length>25?c.substr(0,24)+"...":c)+"</a>"+e}).replace(/\B\@([A-Z0-9_]{1,15})/gi,'@<a class="tf-at" href="http://twitter.com/$1">$1</a>').replace(/\B\#([A-Z0-9_]+)/gi,'<a class="tf-hashtag" href="http://search.twitter.com/search?q=%23$1">#$1</a>')},
D=function(a){if(/^(\w\w\w) (\w\w\w) (\d\d?) (\d\d?:\d\d?:\d\d?) ([\+\-]\d+) (\d\d\d\d)$/i.test(a))a=a.replace(/^(\w\w\w) (\w\w\w) (\d\d?) (\d\d?:\d\d?:\d\d?) ([\+\-]\d+) (\d\d\d\d)$/i,"$1, $3 $2 $6 $4 $5");var c=new Date,e=new Date;c.setTime(Date.parse(a));var h=e.getDate(),r=e.getMonth()+1,F=e.getFullYear(),G=e.getHours(),n=e.getMinutes();e=e.getSeconds();var x=c.getDate(),y=c.getMonth()+1,z=c.getFullYear(),H=c.getHours(),I=c.getMinutes();a=c.getSeconds();if(z==F&&y==r&&x==h){c=G-H;if(c>0)return c+
" hour"+(c>1?"s":"")+" ago";n=n-I;if(n>0)return n+" minute"+(n>1?"s":"")+" ago";a=e-a;return a+" second"+(a>1?"s":"")+" ago"}else return y+"/"+x+"/"+z},J=function(a){A();if(a.screen_name)p.html(b.header.replace(/_tp_/g,"http://twitter.com/"+a.screen_name).replace(/_fr_/g,a.friends_count).replace(/_fo_/g,a.followers_count).replace(/_ti_/g,a.profile_image_url));else if(b.debug)p.html('<b style="color:red">Error:'+(a.error?a.error:"unkown")+"</b>")},A=function(){if(!t){t=1;g.html("");b.info&&g.append(b.info);
if(b.header)p=d('<div class="tf-header"></div>').appendTo(g);j=d('<div class="tf-users"></div>').appendTo(g);if(b.tweet)m=d('<div class="tf-tweet"></div>').appendTo(g)}};b.header&&d.ajax({url:"http://api.twitter.com/1/users/show.json",data:{screen_name:b.username},success:J,dataType:"jsonp",cache:true});d.ajax({url:b.friends?"http://api.twitter.com/1/statuses/friends.json":"http://api.twitter.com/1/statuses/followers.json",data:{screen_name:b.username,cursor:-1},success:function(a){if(a.users){f=
a.users;if(f.length!=0){if(f.length>b.users_max)f.length=b.users_max;f=f.reverse();A();i=-1;q()}}else if(b.debug)g.html('<b style="color:red">Error:'+(a.error?a.error:"unkown")+"</b>")},dataType:"jsonp",cache:true})})}})(jQuery);jQuery(document).ready(function(){jQuery("div.twitter-friends").twitterFriends()});
