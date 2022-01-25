import {makeStyles} from "@material-ui/core";
import {Container, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import { Delete, ModeEdit } from '@mui/icons-material';
import {useDispatch, useSelector} from "react-redux";
import {getBoxAsync, selectBoxes, startEditElement, deleteElement} from "./boxesSlice";

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

const BoxesList = () => {
    const {listStyle, cellStyle,  deleteButtonStyle, editButtonStyle} = useStyles();

    const dispatch = useDispatch();
    dispatch(getBoxAsync());
    const list = useSelector(selectBoxes);

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
                                        {row['address']}
                                    </TableCell>
                                    <TableCell >
                                        {row['boxStatus']}
                                    </TableCell>
                                    <TableCell>
                                        {row['deliveryIDs']}
                                    </TableCell>
                                    <TableCell>
                                        {row['raspberryPiID']}
                                    </TableCell>
                                    <TableCell>
                                        <button type="button" className={editButtonStyle} onClick={() => dispatch(startEditElement(row))}>
                                            <ModeEdit/>
                                        </button>
                                        <button type="button" className={deleteButtonStyle}  onClick={() => dispatch(deleteElement(row))}>
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

export default BoxesList;