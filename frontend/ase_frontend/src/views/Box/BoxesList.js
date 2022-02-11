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
import {getBoxesAsync, selectBoxes, startEditElement} from "./boxesSlice";
import React, {useEffect, useState} from "react";
import {deleteBoxAsync} from "./boxesSlice";
import Button from "@mui/material/Button";
import {parseJwt, getCookie} from '../tokenReader';

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

const listToString = (stringList) => {

    if(stringList === undefined || stringList.length===0){
        return "—";
    } else {
        var res = stringList[0].toString();
        for(var i=1; i< stringList.length;i++ ){
            res = res + ", " + stringList[i].toString();
        }
        return res;
    }
}

const addressToString = (addressObject) => {
    if(addressObject !== undefined)
        return addressObject.streetName.toString() + " " + addressObject.streetNumber.toString() + "\n" + addressObject.postcode.toString() + " " + addressObject.city.toString() + "\n" + addressObject.country.toString();
    else return "";
}

const BoxesList = () => {
    const {listStyle, cellStyle,  deleteButtonStyle, editButtonStyle} = useStyles();

    const dispatch = useDispatch();
    //dispatch(getBoxesAsync());
    useEffect(() => dispatch(getBoxesAsync()), [dispatch]);
    const list = useSelector(selectBoxes);

    const[searchId, setSearchId] = useState("");

    const role = parseJwt(getCookie("jwt")).roles;

    const reloadData = () => {
        dispatch(getBoxesAsync());
    };

    const handleChangeSearchId = (e) => {
        setSearchId(e.target.value);
    }

    const columns = [
        { id: 'id', label: 'Id', minWidth: 30},
        { id: 'address', label: 'Adress', minWidth: 80 },
        { id: 'boxStatus', label: 'Box Status', minWidth: 80},
        { id: 'deliveryIDs', label: 'Delivery IDs', minWidth: 80 },
        { id: 'raspberryPiID', label: 'RaspberryPi ID', minWidth: 80 },
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
                            return !(role!=="ROLE_DISPATCHER" && col.id === "buttons");
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
                    {list.filter((elem) => {
                        if(searchId !== "") {
                            const deliveries = elem.deliveryIDs;
                            var res1 = false;
                            for (let delId of deliveries) {
                                const id = delId.toString();
                                var res2 = true;
                                for (var i = 0; i < searchId.length; i++) {
                                    res2 = res2 && id.charAt(i) === searchId.charAt(i);
                                }
                                res1 = res1 || res2;
                            }
                            return res1;
                        } else {
                            return true;
                        }
                    }).map(row => {
                            return (
                                <TableRow hover role="checkbox" tabIndex={-1} key={row.id} className={cellStyle}>

                                    <TableCell>
                                        {row.id}
                                    </TableCell>
                                    <TableCell>
                                        {addressToString(row.address)}
                                    </TableCell>
                                    <TableCell >
                                        {row.boxStatus}
                                    </TableCell>
                                    <TableCell>
                                        {listToString(row.deliveryIDs)}
                                    </TableCell>
                                    <TableCell>
                                        {row.raspberryPiID}
                                    </TableCell>
                                    { (role === "ROLE_DISPATCHER") &&
                                        <TableCell>
                                            <button type="button" className={editButtonStyle}
                                                    onClick={() => dispatch(startEditElement(row))}>
                                                <ModeEdit/>
                                            </button>
                                            <button type="button" className={deleteButtonStyle}
                                                    onClick={() => dispatch(deleteBoxAsync(row))}>
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

export default BoxesList;