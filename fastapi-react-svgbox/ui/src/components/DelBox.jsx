import { useMutation } from "@apollo/client";

import { DELETE_BOX, DELETE_ALL_BOX } from "../resolvers";
import { Counter } from "./SvgContainer";

const DeleteOneButton = () => {
    const [deleteBox] = useMutation(DELETE_BOX);
    
   return (
    <div className="flex flex-column flex-row@xs gap-xs">
        <button id={"del-one"} className="btn noClick" 
            onClick={(e) =>{
                const node_id = e.currentTarget.getAttribute("data-box-id");
                e.currentTarget.removeAttribute("data-box-id");
                e.currentTarget.classList.add("noClick");
                const rect = document.getElementById(node_id);
                rect.remove();
                deleteBox(
                    {
                        variables: {
                            id: String(node_id)
                        }
                    }
                ); 
                Counter(-1);
                return false
            }} 
            
            >
            Delete
        </button>
    </div>
   );
}

const DeleteButton = () => {
    const [deleteAllBox] = useMutation(DELETE_ALL_BOX);
   return (
    <div className="flex flex-column flex-row@xs gap-xs">
        <button id={"del-all"} className="btn" 
            onClick={() =>{
                const svg = document.getElementsByTagNameNS("http://www.w3.org/2000/svg", "svg")[0];
                while (svg.lastChild) {
                    svg.removeChild(svg.lastChild);
                }
                 deleteAllBox(
                    {
                        variables: {
                        }
                    }
                );
                Counter(0);
            }} 
            >
            Clear
        </button>
    </div>
   );
}

export {DeleteButton, DeleteOneButton};