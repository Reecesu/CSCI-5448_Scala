import React, { useState } from 'react';
import { Button, TextField, FormControl, InputLabel, Select, MenuItem, Grid } from '@mui/material';
// import '@material-ui/core/Grid';


/**
 * a representaiton of the scala expression and its values
 * handles step forward and step back logic
 * 
 * https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Classes
 * 
 * 
 * Handles logic to display things like: 
    *
    * 1 + 2 * 3
      * 1 + 2 * 3 —> 1 + 6
        * 1 + 6
         * 1 + 6 —> 7
 *  
 * Returns 7
 * 
 * 
 * 
 * 
 */


class ScalaExpr {

    constructor(index, expressions) {
        this.updateRight = false;
        this.index = index;
        this.expressions = expressions;
        return;
    }

    reset(expr) {
        this.expressions = [];
        this.index = 0;
        this.updateRight = false;
    }

    /**
     * inc
     * @param props 
     */

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
              props.onNewExpressionChange(this.getExpr());
          }
        } else {
          props.onExpressionChange(this.getExpr());
          props.onNewExpressionChange(undefined);
        }
        console.log(this.updateRight);
        console.log(this.index);
    }

    /**
     * dec
     * 
     * inverts the inc logic
     * 
     * TODO: consider a command pattern here
     * 
     * @param props 
     */
    dec(props) {
        this.updateRight = !this.updateRight;
        if (this.updateRight) {
          this.index = this.index - 1;
          const min = 0;
          if (min > this.index) {
              this.index = min;
              this.updateRight = !this.updateRight;
          } else {
              props.onExpressionChange(this.getExpr());
              props.onNewExpressionChange(this.getNextExpr());
          }
        } else {
          props.onExpressionChange(this.getExpr());
          props.onNewExpressionChange(undefined);
        }
        console.log(this.updateRight);
        console.log(this.index);
    }

    /**
     * getExpr
     * @returns the current expression
     */
    getExpr() {
      return this.getExprI(this.index)
    }

    /**
     * getNextExpr
     * 
     * assumes that a next epression exists. Does not throw error if DNE
     * 
     * @returns the next expression
     */
    getNextExpr() {
      return this.getExprI(this.index + 1)
    }

    /**
     * getExprI
     * @param i 
     * @returns the expression at index $i
     */
    getExprI(i) {
      const s = this.expressions[i]; 
      return s.replace('"', 'hello')
    }
}


// a single instance
// TODO: Consider js singlton pattern for this.
const scalaExpr = new ScalaExpr(0, []);


/**
 * ExpressionInput
 * 
 * a housing for core logic as mandated by react framework
 * 
 * @param props 
 * @returns 
 */
function ExpressionInput(props) {
  // https://legacy.reactjs.org/docs/hooks-state.html
  const [userExpression, setUserExpression] = useState(undefined);
  const [scopingCondition, setScopingCondition] = useState("");
  const [typeCondition, setTypeCondition] = useState("");
  const [lazyEagerCondition, setLazyEagerCondition] = useState("");

  /**
   * handleSubmit
   * 
   * on event click "Send"
   * 
   * @param e 
   */
  const handleSubmit = async (e) => {

    e.preventDefault();

    props.onExpressionChange(undefined);
    props.onNewExpressionChange(undefined);

    scalaExpr.reset();

    // set to true for quicker development needs
    const debugE = false;
    const debugExpr = "1 + 2 * 3";

    const debugC = false;
    const debugEvaluationConditions = {
            scope: "lexical",
            types: "implicit",
            lazyEager: "lazy"
        };

    const userEvaluationConditions = {
      scope: scopingCondition,
      types: typeCondition,
      lazyEager: lazyEagerCondition
    }

    const response = await fetch(`${process.env.REACT_APP_BACKEND_URL}/evaluate`, {
      method: 'POST',
      mode: 'cors',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
          evaluationConditions: (debugC) ? debugEvaluationConditions : userEvaluationConditions,
          expression: (debugE) ? debugExpr : userExpression
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
    props.onNewExpressionChange(undefined);
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
      scalaExpr.inc(props);
      return;
  };
 
    /**
   * page layout
   * 
   *    box              dropdown
   *                     dropdown
   *                     dropdown
   *  btn  btn btn
   *   
   *       box          box
   */

  return (
    <div>
      <form onSubmit={handleSubmit} style={formStyle}>
        {/* https://mui.com/material-ui/api/grid/ */}
        <Grid container rowSpacing={1}>
          <Grid item xs={7}> 
            <TextField
                label="Expression"
                value={userExpression}
                onChange={(e) => setUserExpression(e.target.value)}
                style={textFieldStyle}
                fullWidth
                multiline
                rows={4}
                />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={4}>
            <Grid container rowSpacing={1}>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="scoping-conditions-select-label">Scoping Conditions</InputLabel>
                  <Select
                    labelId="scoping-conditions-select-label"
                    id="scoping-conditions-select"
                    value={scopingCondition}
                    label="scopingConditions"
                    onChange={(e) => setScopingCondition(e.target.value) }
                  >
                    <MenuItem value={"lexical"}>lexical/static</MenuItem>
                    <MenuItem value={"dynamic"}>dynamic</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="type-conditions-select-label">Type Conditions</InputLabel>
                  <Select
                    labelId="type-conditions-select-label"
                    id="type-conditions-select"
                    value={typeCondition}
                    label="TypeConditions"
                    onChange={(e) => setTypeCondition(e.target.value) }
                  >
                    <MenuItem value={"none"}>none</MenuItem>
                    <MenuItem value={"implicit"}>implicit</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <FormControl fullWidth>
                  <InputLabel id="lazyEagerCondition-conditions-select-label">Lazy/Eager Conditions</InputLabel>
                  <Select
                    labelId="lazyEagerCondition-conditions-select-label"
                    id="lazyEagerCondition-conditions-select"
                    value={lazyEagerCondition}
                    label="lazyEagerCondition"
                    onChange={(e) => setLazyEagerCondition(e.target.value) }
                  >
                    <MenuItem value={"lazy"}>lazy</MenuItem>
                    <MenuItem value={"eager"}>eager</MenuItem>
                  </Select>
                </FormControl>
                </Grid>
              </Grid> {/* End top row's right column */}
          </Grid>
        </Grid> {/* End top row */}
        <div>
          {/* https://mui.com/material-ui/react-button/ */}
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
