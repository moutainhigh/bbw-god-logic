var r = ["q", "w", "e", "8", "a", "s", "2", "d", "z", "x", "9", "c", "7", "p", "5", "i", "k", "3", "m", "j", "u", "f", "r", "4", "v", "y", "l", "t", "n", "6", "b", "g", "h"];
var b = "o";
var binLen = r.length;
var s = 6;
//数字转编码
function idToCode(id) {
	var str="";
	while ((id / binLen) > 0) {
		var ind = id % binLen;
		str = r[ind]+str;
		id = (id-ind)/binLen;
	}
	str="p"+str;
	return str;
}

