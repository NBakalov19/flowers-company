const URLS = {
    batches: '/flowers/api/todays-batches',
};

const batchRow = ({id, variety, teamSupervisor, fieldName, trays, bunchesPerTray, warehouse}, index) =>
    `<tr class="row mx-auto">
        <th class="col-sm-1 text-center text-dark font-weight-bold">${index + 1}</th>
        <td class="col-md-1 text-center text-dark">${warehouse}</td>
        <td class="col-md-1 text-center text-dark">${variety}</td>
        <td class="col-md-1 text-center text-dark">${trays}</td>
        <td class="col-md-1 text-center text-dark">${bunchesPerTray}</td>
        <td class="col-md-2 text-center text-dark">${teamSupervisor}</td>
        <td class="col-md-2 text-center text-dark">${fieldName}</td>
        <td class="col-md-3 text-center text-dark">
            <div class="row justify-content-center">
                <a href="/flowers/move-batch/${id}" 
                    class="btn btn-primary text-light mx-1">Move Batch</a>
                <a href="/flowers/edit-batch/${id}"
                   class="btn btn-warning text-light mx-1">Edit</a>
                <a href="/flowers/delete-batch/${id}"
                   class="btn btn-danger text-light mx-1">Delete</a>
            </div>
        </td>
    <tr>`
;

fetch(URLS.batches)
    .then((response) => response.json())
    .then((batches) => {
        let result = '';
        batches.forEach((batch, index) => {
            let batchString = batchRow(batch, index);
            result += batchString;
        });
        document.getElementById(`tableBody`)
            .innerHTML = result;
    })
    .catch((err) => console.log(err));