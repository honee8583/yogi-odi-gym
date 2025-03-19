const csrfToken = $("meta[name='_csrf']").attr("content");
const csrfHeader = $("meta[name='_csrf_header']").attr("content");

document.getElementById('submit-btn').onclick(function () {
    validateForm();
    registerBoard();
});

function validateForm() {
    let title = document.getElementById('title').value;
    let context = document.getElementById('context').value;
    let categoryId = document.querySelector("input[name='categoryId']:checked");

    if (!title.trim()) {
        alert("제목을 입력해주세요.");
        return false;
    }

    if (!context.trim()) {
        alert("내용을 입력해주세요.");
        return false;
    }

    if (!categoryId) {
        alert("카테고리를 선택해주세요.");
        return false;
    }

    return true;
}

function registerBoard() {
    const title = document.getElementById('title').value;
    const context = document.getElementById('context').value;
    const categoryId = document.querySelector('input[name="categoryId"]:checked').value;

    let data = {
        title: title,
        context: context,
        categoryId: categoryId
    }

    console.log(data);

    fetch('/api/board/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => {
            console.log(data.message);
            window.location.href='/board';
        })
        .catch(error => console.error(error.message));
}