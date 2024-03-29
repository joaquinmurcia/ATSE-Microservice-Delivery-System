import React, {useEffect, useState} from 'react';
import {makeStyles} from "@material-ui/core";
import {
    Container,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField
} from '@mui/material';
import { Delete, ModeEdit } from '@mui/icons-material';
import {useDispatch, useSelector} from "react-redux";
import {
    deleteDeliveryAsync, editDeliveryAsync,
    getDeliveriesAsync,
    selectDeliveries,
    startEditElement
} from "./deliveriesSlice";
import Button from "@mui/material/Button";
import {getCookie, parseJwt} from "../tokenReader";

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

    const dispatch = useDispatch();

    const[searchId, setSearchId] = useState("");

    useEffect(() => dispatch(getDeliveriesAsync()), [dispatch]);
    //dispatch(getDeliveriesAsync());

    const list = useSelector(selectDeliveries);

    const reloadData = () => {
        dispatch(getDeliveriesAsync());
    };

    const handleChangeSearchId = (e) => {
        setSearchId(e.target.value);
    }

    const columns = [
        { id: 'id', label: 'Tracking Number', minWidth: 30},
        { id: 'targetBox', label: 'Box', minWidth: 80 },
        { id: 'targetCustomer', label: 'Customer', minWidth: 80},
        { id: 'responsibleDeliverer', label: 'Responsible Deliverer', minWidth: 80 },
        { id: 'deliveryStatus', label: 'Status', minWidth: 80 },
        { id: 'buttons', label: 'Actions', minWidth: 90 },
    ];

    return (
        <Container>
            <Paper  sx={{border: 1, borderRadius: 1}}>
                <TextField sx={{minWidth: 120, margin: 1}} size="small" name="search" label="Search Tracking Number" value={searchId} onChange={handleChangeSearchId}/>
                <TableContainer>
                    <Table stickyHeader aria-label="sticky table" className={listStyle}>
                        <TableHead>
                            <TableRow>
                                {columns.filter((col)=>{
                                    const role = parseJwt(getCookie("jwt")).roles;
                                    return !(role==="ROLE_CUSTOMER" && col.id === "buttons")

                                }).map((column) => (
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
                            {
                                list.filter((elem) => {
                                    const id = elem.id.toString();
                                    var res = true;
                                    for (var i = 0; i < searchId.length; i++) {
                                        res = res && id.charAt(i) === searchId.charAt(i);
                                    }
                                    return res;
                                }).map(row => {
                                return (
                                    <TableRow hover role="checkbox" tabIndex={-1} key={row.id} className={cellStyle}>
                                        <TableCell>
                                            {row.id}
                                        </TableCell>
                                        <TableCell>
                                            {row.targetBox}
                                        </TableCell>
                                        <TableCell >
                                            {row.targetCustomer}
                                        </TableCell>
                                        <TableCell>
                                            {row.responsibleDeliverer}
                                        </TableCell>
                                        <TableCell>
                                            {row.deliveryStatus}
                                        </TableCell>
                                        { parseJwt(getCookie("jwt")).roles === "ROLE_DELIVERER" &&
                                            <Button sx={{minWidth: 100, margin: 1}} variant="contained" size="small" color="success"  onClick={()=> dispatch(editDeliveryAsync({id: row.id}))}>Deposit</Button>
                                        }
                                        { parseJwt(getCookie("jwt")).roles === "ROLE_DISPATCHER" &&
                                            <TableCell>
                                                <button type="button" className={editButtonStyle}
                                                        onClick={() => dispatch(startEditElement(row))}>
                                                    <ModeEdit/>
                                                </button>
                                                <button type="button" className={deleteButtonStyle}
                                                        onClick={() => dispatch(deleteDeliveryAsync(row))}>
                                                    <Delete/>
                                                </button>
                                            </TableCell>
                                        }
                                    </TableRow>
                                );
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
                <Button sx={{minWidth: 100, minHeight: 30, margin: 1}} variant="contained" size="small" color="success" onClick={()=> reloadData()}>Reload Data</Button>
            </Paper>
        </Container>
    );
};

export default DeliveriesList;