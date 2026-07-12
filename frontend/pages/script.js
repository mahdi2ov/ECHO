/* theme */
if (!localStorage.getItem('theme')){
  document.documentElement.setAttribute('data-theme', 'dark');
}
const themesButton = document.querySelectorAll('.themes .item');
const setTheme = (themeElement) => {
  themesButton.forEach(button => {
    button.classList.remove('active');
  });
  themeElement.classList.add('active');
  const themeAttribute = themeElement.getAttribute('data-theme');
  document.documentElement.setAttribute('data-theme', themeAttribute);
  localStorage.setItem('theme', themeAttribute);
};
themesButton.forEach(button => {
  button.addEventListener('click', (e) => {
    setTheme(e.currentTarget);
  });
});
window.addEventListener('DOMContentLoaded', () => {
  const savedTheme = localStorage.getItem('theme');
  if(savedTheme){
    const savedButton = document.querySelector(`.themes .item[data-theme="${savedTheme}"]`);
    if (savedButton) {
      setTheme(savedButton);
    }else {
      document.documentElement.setAttribute('data-theme', 'dark');
    }
  }else {
    document.documentElement.setAttribute('data-theme', 'dark');
    const darkButton = document.querySelector('.themes .item[data-theme="dark"]');
    if (darkButton) {
      darkButton.classList.add('active');
    }
  }
});


/* message input */
const textarea = document.getElementById('message');
textarea.addEventListener('input', () => {
  textarea.style.height = 'auto';
  textarea.style.height = textarea.scrollHeight + 'px';
});
textarea.addEventListener('keydown', (e) => {
  if (e.key === 'Enter' && e.ctrlKey) {
    e.preventDefault();
    // sendMessage();
    textarea.value = '';
    textarea.style.height = 'auto';
  }
});

/* menu */
const menuButton = document.querySelector('.menu.button');
const hamburgerMenu =document.querySelector('.hamburger-menu');
menuButton.addEventListener('click',() => {
  hamburgerMenu.classList.toggle('open');
  menuButton.classList.toggle('active');
});

/* chat info */
const close = document.querySelector('.info .close');
const chatInfo = document.querySelector('.info');
const headerInfo = document.querySelector('.chat .chat-info');
const pageMain = document.querySelector('.page-main');
close.addEventListener('click',() => {
  chatInfo.classList.remove('open');
  pageMain.style.gridTemplateColumns = '360px auto';
});
headerInfo.addEventListener('click',() => {
  chatInfo.classList.add('open');
  pageMain.style.gridTemplateColumns = '360px auto 360px';
});

/* search */
const searchInput = document.querySelector('#search');
const searchResult = document.querySelector('.search-result');
const chatList = document.querySelector('.chat-list');
const newMessage = document.querySelector('.sidebar .new-message');
const backSearchButton = document.querySelector('.header.chat .back.button');
searchInput.addEventListener('focus',() =>{
  searchResult.classList.add('open');
  chatList.classList.remove('open');
  newMessage.classList.remove('open');
  menuButton.classList.remove('open');
  backSearchButton.classList.add('open');
  hamburgerMenu.classList.remove('open');
  menuButton.classList.remove('active');
});
backSearchButton.addEventListener('click',() =>{
  searchResult.classList.remove('open');
  chatList.classList.add('open');
  newMessage.classList.add('open');
  menuButton.classList.add('open');
  backSearchButton.classList.remove('open');
});

/* setting */
const settingButton = document.querySelector('.sidebar .hamburger-menu .setting-button');
const setting = document.querySelector('.sidebar .settings');
const headerChatList = document.querySelector('.header.chat');
const headerSetting = document.querySelector('.header.setting');
const backSettingButton = document.querySelector('.header.setting .back.button');
settingButton.addEventListener('click',()=>{
  setting.classList.add('open');
  chatList.classList.remove('open');
  headerChatList.classList.remove('open');
  headerSetting.classList.add('open');
  newMessage.classList.remove('open');
  hamburgerMenu.classList.remove('open');
});

backSettingButton.addEventListener('click',()=>{
  setting.classList.remove('open');
  chatList.classList.add('open');
  headerChatList.classList.add('open');
  headerSetting.classList.remove('open');
  newMessage.classList.add('open');
  menuButton.classList.remove('active');
});

/* search in chat */
const searchButton = document.querySelector('.chat .chat-info .search');
const chatInfoText = document.querySelector('.chat .chat-info .text');
const chatInfoSearchInput = document.querySelector('.chat .chat-info .input-search');
const closeInfoSearchButton = document.querySelector('.chat .chat-info .close');
searchButton.addEventListener('click',()=>{
  searchButton.classList.remove('open');
  chatInfoText.classList.remove('open');
  chatInfoSearchInput.classList.add('open');
});
closeInfoSearchButton.addEventListener('click',()=>{
  searchButton.classList.add('open');
  chatInfoText.classList.add('open');
  chatInfoSearchInput.classList.remove('open');
});