import React, { useState } from 'react';
import { Button, TextField } from '@mui/material';


class ScalaExpr {

    constructor(index, expressions) {
        this.updateRight = false;
        this.index = index;
        this.expressions = expressions;
        return;
    }

    inc(props) {
        this.updateRight = !this.updateRight;
        if (this.updateRight) {
          this.index = this.index + 1;
          const max = this.expressions.length - 1; 
          if (max < this.index) {
              this.index = max;
              this.updateRight = !this.updateRight;
              props.onNewExpressionChange("You cannot step on a value");
          } else {
              props.onNewExpressionChange(this.expressions[this.index]);
          }
        } else {
          props.onExpressionChange(this.expressions[this.index]);
          props.onNewExpressionChange('');
        }
        console.log(this.updateRight);
        console.log(this.index);
    }

    dec(props) {
        this.updateRight = !this.updateRight;
        if (this.updateRight) {
          this.index = this.index - 1;
          const min = 0;
          if (min > this.index) {
              this.index = min;
              this.updateRight = !this.updateRight;
          } else {
              props.onExpressionChange(this.expressions[this.index]);
              props.onNewExpressionChange(this.expressions[this.index + 1]);
          }
        } else {
            // SPWI: still off by something...
          props.onExpressionChange(this.expressions[this.index]);
          props.onNewExpressionChange('');
        }
        console.log(this.updateRight);
        console.log(this.index);
    }

    getExpr() {
        return this.expressions[this.index]; 
    }
}

const scalaExpr = new ScalaExpr(0, []);

function ExpressionInput(props) {
  const [userExpression, setUserExpression] = useState('');
  // const [result] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    console.log(userExpression);
    const debug = false;
    const debugExpr = "1 + 2 * 3";
    const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/evaluate`, {
      method: 'POST',
      mode: 'cors',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
          // TODO: find out from user data
          // // scope: lexical | dynamic
          // // type: implicite | none
          // // lazyEager: lazy | eager
          evaluationConditions: {
              scope: "lexical",
              types: "none",
              lazyEager: "eager"
          },
          expression: (debug) ? debugExpr : userExpression
      })
    });


    /**
     * {
     *  "expression": "<>",
     *  "value": "<>",
     *  "steps": ["<>"]
     * }
     */
    const data = await response.json();
    console.log('Server response:', data);
    scalaExpr.expressions = data.steps;
    props.onExpressionChange(scalaExpr.getExpr());
    props.onNewExpressionChange('');
  };

  const formStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    marginBottom: '10px',
  };

  const textFieldStyle = {
    marginBottom: '10px',
    marginLeft: '10px',
  };

  const buttonStyle = {
    marginLeft: '10px',
  };

  const handleBack = () => {
      scalaExpr.dec(props);
      return;
  };

  const handleNext = () => {
      // 1 + 2 * 3
      // 1 + 2 * 3 —> 1 + 6
      // 1 + 6
      // 1 + 6 —> 7
      // 7
      scalaExpr.inc(props);
      return;
  };

  return (
    <div>
      <form onSubmit={handleSubmit} style={formStyle}>
      <TextField
          label="Expression"
          value={userExpression}
          onChange={(e) => setUserExpression(e.target.value)}
          style={textFieldStyle}
          fullWidth
          multiline
          rows={4}
        />
        <div>
          <Button type="submit" variant="contained" style={buttonStyle}>
            Send
          </Button>
          <Button onClick={handleBack} variant="contained" style={buttonStyle}>
            ← Back
          </Button>
          <Button onClick={handleNext} variant="contained" style={buttonStyle}>
            Next →
          </Button>
        </div>
      </form>
    </div>
  );
}

export default ExpressionInput;
