import React from 'react';
import Paper from '@mui/material/Paper';
import { Typography } from '@mui/material';

export default function Evaluation() {
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
                New Expression
            </Typography>
        </Paper>
    );
}
