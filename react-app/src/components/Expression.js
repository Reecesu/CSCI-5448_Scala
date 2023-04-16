import React from 'react';
import Paper from '@mui/material/Paper';
import { Typography } from '@mui/material';

export default function ExecutionSteps() {
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
            <Typography variant="h10" sx={{ padding: 1}}>
                Expression
            </Typography>
        </Paper>
    );
}

