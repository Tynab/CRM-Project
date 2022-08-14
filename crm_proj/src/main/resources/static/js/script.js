async function init() {
    var url = new URL(window.location.href)
    var error = url.searchParams.get("flag")
    var isTrueSet = (error === 'true')
    if (isTrueSet) {
        var msg = url.searchParams.get("msg")
        Toastify({ text: msg, position: "center" }).showToast()
    }
}

init()
