export async function copyToClipboard(textToCopy: string) {
    // method for old browsers (and without exceptions handling)
    const textArea = document.createElement('textarea');
    textArea.value = textToCopy;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
}
