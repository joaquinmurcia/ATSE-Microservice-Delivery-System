import React from 'react';
import {makeStyles} from "@material-ui/core";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import { Delete, ModeEdit } from '@mui/icons-material';

const initialList = [
    {     id : '1',
        targetBox : 'Garching',
        targetCustomer : 'Josef',
        responsibleDriver : 'Erik',
        deliveryStatus : 'pickedUp'},
    { id : '2',
        targetBox : 'Garching',
        targetCustomer : 'Josef',
        responsibleDriver : 'Erik',
        deliveryStatus : 'pickedUp'},
    { id : '3',
        targetBox : 'Garching',
        targetCustomer : 'Josef',
        responsibleDriver : 'Erik',
        deliveryStatus : 'pickedUp' },
];

const useStyles = makeStyles(() => {
    return {
        listStyle: {
            backgroundColor: '#ffffff',
            listStyleType: 'none',
            border: 'solid gray 2px',
        },
        cellStyle:{
           border: 'solid red 2px'
        },
        deleteButtonStyle: {
            borderRadius: '5px',
            borderStyle: 'solid',
            margin: '2px',
            width: '40px',
            height: '40px',
            borderColor: '#f32e2e',
            color: '#f32e2e',
            backgroundColor: 'rgba(243,46,46,0.15)',

        },
        editButtonStyle: {
            borderRadius: '5px',
            borderStyle: 'solid',
            margin: '2px',
            width: '40px',
            height: '40px',
            borderColor: '#f3bb2e',
            color: '#f3bb2e',
            backgroundColor: 'rgba(243,187,46,0.15)',
        }
    };
});

const DeliveriesList = () => {
    const {listStyle, cellStyle,  deleteButtonStyle, editButtonStyle} = useStyles();
    const [list, setList] = React.useState(initialList);

    const columns = [
        { id: 'id', label: 'Id', minWidth: 100},
        { id: 'targetBox', label: 'Box', minWidth: 170 },
        { id: 'targetCustomer', label: 'Customer', minWidth: 100},
        { id: 'responsibleDriver', label: 'Responsible Driver', minWidth: 170 },
        { id: 'deliveryStatus', label: 'Status', minWidth: 170 },
        { id: 'buttons', label: 'Actions', minWidth: 170 },
    ];

    const deleteRow = id => {
        setList(list.filter(item => item.id !== id))
    };

    function editElement(id) {
        setList(list.filter(item => item.id !== id))
    };

    return (
        <TableContainer sx={{ maxHeight: 440 }}>
            <Table stickyHeader aria-label="sticky table" className={listStyle}>
                <TableHead>
                    <TableRow>
                        {columns.map((column) => (
                            <TableCell
                                key={column.id}
                                align={column.align}
                                style={{ minWidth: column.minWidth }}
                            >
                                {column.label}
                            </TableCell>
                        ))}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {list.map(row => {
                            return (
                                <TableRow hover role="checkbox" tabIndex={-1} key={row.id} className={cellStyle}>

                                    <TableCell>
                                        {row['id']}
                                    </TableCell>
                                    <TableCell>
                                        {row['targetBox']}
                                    </TableCell>
                                    <TableCell >
                                        {row['targetCustomer']}
                                    </TableCell>
                                    <TableCell>
                                        {row['responsibleDriver']}
                                    </TableCell>
                                    <TableCell>
                                        {row['deliveryStatus']}
                                    </TableCell>
                                    <TableCell>
                                        <button type="button" className={editButtonStyle} onClick={() => editElement(row.id)}>
                                            <ModeEdit/>
                                        </button>
                                        <button type="button" className={deleteButtonStyle}  onClick={() => deleteRow(row.id)}>
                                            <Delete/>
                                        </button>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default DeliveriesList;