import {makeStyles} from "@material-ui/core";
import {Container, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import { Delete, ModeEdit } from '@mui/icons-material';
import React, { useState } from 'react';
import {useDispatch, useSelector} from "react-redux";
import {getDeliveriesAsync, selectDeliveries} from "./deliveriesSlice";

const useStyles = makeStyles(() => {
    return {
        listStyle: {
            //backgroundColor: '#ffffff',
            listStyleType: 'none',
            //border: 'solid gray 2px',
            overflowX: 'hidden'
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
    //const [list2, setList] = React.useState([]);

    useDispatch(getDeliveriesAsync());
    const list = useSelector(selectDeliveries);

    const columns = [
        { id: 'id', label: 'Id', minWidth: 30},
        { id: 'targetBox', label: 'Box', minWidth: 80 },
        { id: 'targetCustomer', label: 'Customer', minWidth: 80},
        { id: 'responsibleDriver', label: 'Responsible Driver', minWidth: 80 },
        { id: 'deliveryStatus', label: 'Status', minWidth: 80 },
        { id: 'buttons', label: 'Actions', minWidth: 90 },
    ];

    const deleteRow = id => {
        //setList(list.filter(item => item.id !== id))
    };

    function editElement(id) {
        //setList(list.filter(item => item.id !== id))
    };

    return (
        <Container>
        <Paper  sx={{border: 1, borderRadius: 1}}>
        <TableContainer>
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
        </Paper>
        </Container>
    );
};

export default DeliveriesList;