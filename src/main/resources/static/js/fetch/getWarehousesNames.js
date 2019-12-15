fetch('/api/warehouses/all')
    .then((response) => response.json())
    .then((json) => {

        json.forEach((warehouse) => $('#warehouse')
            .append(`<option value="${warehouse.id}">${warehouse.name}</option>`))
    })
    .catch((err) => console.log(err));