$('#shareme').sharrre({
  share: {
    googlePlus: true,
    facebook: true,
    twitter: true,
    digg: true,
    delicious: true
  },
  enableTracking: true,
  buttons: {
    googlePlus: {size: 'tall'},
    facebook: {layout: 'box_count'},
    twitter: {count: 'vertical'},
    digg: {type: 'DiggMedium'},
    delicious: {size: 'tall'}
  },
  hover: function(api, options){
    $(api.element).find('.buttons').show();
  },
  hide: function(api, options){
    $(api.element).find('.buttons').hide();
  }
});