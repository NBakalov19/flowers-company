fetch('/warehouses/api/all')
    .then((response) => response.json())
    .then((json) => {

        let batchWarehouse = [[${batch.warehouse}]];

        json.forEach((warehouse) => {
                if (batchWarehouse) {
                    $('#warehouse')
                        .append(`<option value="${warehouse.id}" selected>${warehouse.name}</option>`);
                } else {
                    $('#warehouse')
                        .append(`<option value="${warehouse.id}">${warehouse.name}</option>`);
                }
            }
        )
    })
    .catch((err) => console.log(err));