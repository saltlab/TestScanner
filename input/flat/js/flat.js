var Flat = (function(){
  function parse(flatText){
    var items = flatText.split("\n\n");
    var itemsArr = [];
    for(var i = 0; i < items.length; i++){
      var itemText = items[i].trim();
      if(!itemText){
        continue;
      }
      var itemObj = {};
      var keyvals = itemText.split("\n");
      for(var j = 0; j < keyvals.length; j++){
        var keyvalSplit = keyvals[j].split(":");
        itemObj[keyvalSplit[0].trim()] = getValue(keyvalSplit[1]);
      }
      itemsArr.push(itemObj);
    }
    if(itemsArr.length == 1){
      return itemsArr[0];
    }
    return itemsArr;
  }
  function getValue(text){
    //empty
    if(!text.trim()){
      return undefined;
    }
    //number
    if(!isNaN(text)){
      return parseInt(text);
    }
    //array
    if(text.indexOf(",") != -1){
      var textValues = text.split(",");
      var arr = [];
      for(var i = 0; i < textValues.length; i++){
        var value = getValue(textValues[i]);
        if(value || value === 0){
          arr.push(value);
        }
      }
      return arr;
    }
    //date
    var date = new Date(text);
    if(date.getTime() === date.getTime()){
      return date;
    }
    //string
    return text.trim();
  }
  function toJson(text){
    return JSON.stringify(parse(text));
  }
  function toCsv(flatText, headers){
    var items = [].concat(parse(flatText));
    var fields = getFieldList(items);
    return buildCsv(items, fields);
  }
  function getFieldList(items){
    var fields = [];
    for(var i = 0; i < items.length; i++){
      for(var key in items[i]){
        if(fields.indexOf(key) == -1){
          fields.push(key);
        }
      }
    }
    return fields;
  }
  function buildCsv(items, fields){
    var text = "";
    for(var i = 0; i < items.length; i++){
      var line = "";
      for(var j = 0; j < fields.length; j++){
        line += (items[i][fields[j]] || "") + (j < fields.length -1 ? "," : "");
      }
      text += line + (i < items.length -1 ? "\n" : "");
    }
    return text;
  }
  function fromObject(obj){
    var flat = "";
    if(Array.isArray(obj)){
      for(var i = 0; i < obj.length; i++){
        flat += objectToFlat(obj[i]) + "\n\n";
      }
      flat = flat.substr(0, flat.length - 2);
    }else{
      flat = objectToFlat(obj);
    }
    return flat;
  }
  function objectToFlat(obj){
    var flat = "";
    for(key in obj){
      flat += key + ":" + valueToFlat(obj[key]) + "\n";
    }
    flat = flat.substr(0, flat.length - 1);
    return flat;
  }
  function valueToFlat(val){
    if(Array.isArray(val)){
      return val.join(",");
    }else{
      return val;
    }
  }
  return {
    parse : parse,
    toJson : toJson,
    toCsv : toCsv,
    fromObject : fromObject
  };
})();