const Utils = {
    notify(that, type, title, msg) {
        that.$notify({
            group: "main-notifications",
            title: title,
            text: msg,
            type: type,
            classes: "notifications"
        });
    }

}

export default Utils;