/* theme */
if (!localStorage.getItem('theme')) {
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
    if (savedTheme) {
        const savedButton = document.querySelector(`.themes .item[data-theme="${savedTheme}"]`);
        if (savedButton) {
            setTheme(savedButton);
        } else {
            document.documentElement.setAttribute('data-theme', 'dark');
        }
    } else {
        document.documentElement.setAttribute('data-theme', 'dark');
        const darkButton = document.querySelector('.themes .item[data-theme="dark"]');
        if (darkButton) {
            darkButton.classList.add('active');
        }
    }
});
