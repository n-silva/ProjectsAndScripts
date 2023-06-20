import { Counter } from "./SvgContainer";

function onClick(evt) {
  const element = evt.currentTarget;
  const elements =  document.getElementsByClassName("del-box");
  const btn_del =  document.querySelector('#del-one');
  
  if (element.getAttribute("class")){
      element.removeAttribute("class")
      btn_del.classList.add("noClick");
      btn_del.removeAttribute("data-box-id");
  }else{
      btn_del.classList.remove("noClick");
      btn_del.classList.add("btn");
      btn_del.setAttribute("data-box-id",element.id);
      for (const child of elements) {
          child.removeAttribute("class")
      }
      element.setAttribute("class", "del-box");
  }
}

/**
 * Draw the nodes.
 */
class AddBox {
    /**
     * Each time this is called we draw a single node
     * @param {*} svg 
     * @param {*} node 
     */
    static add_node(svg, node){
        const NS = "http://www.w3.org/2000/svg";
        const g = document.createElementNS(NS,"g");
        
        g.setAttributeNS(null, "id", node.id);
        g.setAttributeNS(null, "class", "node");
        g.setAttributeNS(null, "transform", "translate(" + node.x + "," + node.y + ")");
   
        const rect = document.createElementNS(NS,"rect");
        
        rect.setAttributeNS(null, "id", node.id);
        rect.setAttributeNS(null,"width", node.width);
        rect.setAttributeNS(null,"height", node.height);
        rect.setAttributeNS(null,"fill", node.color);
        
        g.appendChild(rect);    
        svg.append(g);

        rect.addEventListener("click", onClick);
        Counter(1);
    }

    /**
     * Each time this is called we draw the added nodes at once
     * @param {*} svg 
     * @param {*} nodes 
     */
    static draw(svg, nodes) {
        nodes.forEach(node => {
                this.add_node(svg, node)
        });
    }
  }
  
  export {AddBox};