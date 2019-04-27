package runtime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class DesiRuntime implements DesiRuntimeConstants{
	
	private Stack<HashMap<String, DataValues>> memoryStack = new Stack<>();
    private List<String> intermediateCode;
    private boolean storeReturn = false;

    private int programCounter = 0;
    private String output = "";

	
	 public DesiRuntime(String intermediateCode) {
	        this.intermediateCode = Arrays.asList(intermediateCode.split("\\n"));
	        System.out.println("intermediate code"+ this.intermediateCode) ;
	    }

	 
	 public void execute() {
		 initializeStackMemory();

	        while(programCounter < intermediateCode.size()) {

	        	programCounter = executeInstructionHandler(intermediateCode.get(programCounter), programCounter) + 1;

	        }

	    }


	    private int executeInstructionHandler(String currentInstruction, int programCounter) {
	    	
	    String[] instructions = splitInstruction(currentInstruction);
		String instructionType = instructions[0];
		
	    switch(instructionType) {
        case STORE_INSTRUCTION:
            executeStoreInstruction(instructions);
            break;
        case WRITE_INSTRUCTION:
        	executePrintInstruction(instructions);
        	break;
        	
        case ADDITION:
        case SUBTRACTION:
        case MULTIPLICATION:
        case DIVISION:
            executeArithmeticOperations(instructions);
            break;
            
        // All Comparison Operations
        case GT:
        case GTE:
    	case LT:
    	case LTE:
    	case EQUAL_EQUAL:
    	case NOT_EQUAL:
        	executeBooleanComparisonOperations(instructions);
        	break;
	    }
	    
		return programCounter;
	}

	    private void executeBooleanComparisonOperations(String[] instruction) {
	    	int leftOperand = getValue(instruction[2]).asInt();
	    	int rightOperand = getValue(instruction[3]).asInt();
	    	
	    	switch(instruction[0]) {
		    	case GT:
		    		setValue(instruction[1], new DataValues(leftOperand > rightOperand));
		    		break;
		    	case GTE:
		    		setValue(instruction[1], new DataValues(leftOperand >= rightOperand));
		    		break;
		    	case LT:
		    		setValue(instruction[1], new DataValues(leftOperand < rightOperand));
		    		break;
		    	case LTE:
		    		setValue(instruction[1], new DataValues(leftOperand <= rightOperand));
		    		break;
		    	case EQUAL_EQUAL:
		    		setValue(instruction[1], new DataValues(leftOperand == rightOperand));
		    		break;
		    	case NOT_EQUAL:
		    		setValue(instruction[1], new DataValues(leftOperand != rightOperand));
		    		break;        
	    	}
	    	
	    }

		private void executeArithmeticOperations(String[] instruction) {
	    	int leftOperand = getWildCardValue(instruction[2]).asInt();
	    	int rightOperand = getWildCardValue(instruction[3]).asInt();
	    	
	    	switch(instruction[0]) {
            case ADDITION:
                setValue(instruction[1], new DataValues(leftOperand + rightOperand));
                break;
            case SUBTRACTION:
                setValue(instruction[1], new DataValues(leftOperand - rightOperand));
                break;
            case MULTIPLICATION:
                setValue(instruction[1], new DataValues(leftOperand * rightOperand));
                break;
            case DIVISION:
                setValue(instruction[1], new DataValues(leftOperand / rightOperand));
                break;
        }
	    	
		}


		private void executePrintInstruction(String[] instruction) {
	    	DataValues printData = getWildCardValue(instruction[1]);

	        if (null != printData) {
	            try {
	                generateOutput(printData.toString());
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    
	    private void generateOutput (String output) {
	        this.output += output + "\n";
	    }


	    private void initializeStackMemory() {
	    	memoryStack.push(new HashMap<>());
	    }
	    
	    private String[] splitInstruction(String instruction) {
	        return instruction.split("\\s");
	    }
	    
	    private void executeStoreInstruction(String[] instruction) {
	        if(instruction[1].equals(ACCUMULATOR_REGISTER)) {
	            setValue(ACCUMULATOR_REGISTER, getWildCardValue(instruction[2]));
	        }
	        else {
	            setValue(instruction[1], getValue(instruction[2]));
	        }
	    }
	    
	    private void setValue(String identifier, DataValues value) {
	        HashMap<String, DataValues> hashMap = memoryStack.peek();
	        hashMap.put(identifier, value);
	        System.out.println(memoryStack);
	        
	    }
	    
	    private boolean isInt(String value) {
	        try {
	            Integer.parseInt(value);
	            return true;
	        } catch (NumberFormatException e) {
	            return false;
	        }
	    }
	    
	    private boolean isBoolean(String value) {
	        try {
	        	Boolean b = Boolean.parseBoolean(value);
	        	if(!b && !value.equalsIgnoreCase("FALSE")) {
	        		 return false;
	        	}
	            return true;
	        } catch (NumberFormatException e) {
	            return false;
	        }
	    }

	    private DataValues getValue(String identifier) {
	        HashMap<String, DataValues> hashMap = memoryStack.peek();
	        return hashMap.get(identifier);
	    }

	    public String getOutputData() {
	        return this.output;
	    }
	    
	    private DataValues getWildCardValue(String value) {
	        if(value.equals("NULL")) {
	            return null;
	        }
	        else if(isInt(value)) {
	            return new DataValues(Integer.parseInt(value));
	        }
	        else if(isBoolean(value)) {
	            return new DataValues(Boolean.parseBoolean(value));
	        }
	        else {
	            return getValue(value);
	        }
	    }
}
