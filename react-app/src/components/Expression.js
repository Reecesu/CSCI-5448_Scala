import React, { useState } from 'react';
import Paper from '@mui/material/Paper';
// import { Typography, Box } from '@mui/material';

const Expression = (props) =>  {
    // const [props, setResultField] = useState('');
    // setResultField(props);
    console.log("HELLO\n\n\n\n" + typeof(props));
    console.log("YO\n\n\n\n" + props);
    console.log("what if...\n\n\n\n");
    return (
        <Paper
            sx={{
            width: '50%',
            height: '100%',
            overflow: 'auto',
            borderWidth: 1,
            borderStyle: 'solid',
            mx: 1,
            }}
            variant="outlined"
        >
        {props.value}
        </Paper>
    );
}

export default Expression;
